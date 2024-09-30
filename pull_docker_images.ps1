# Array of Docker images to pull
$images = @(
    "python:3.12-slim",
    "maven:3.9.9-eclipse-temurin-21",
    "rust:1.80-slim",
    "mcr.microsoft.com/dotnet/sdk:8.0",
    "golang:1.23-bookworm"
)

# Custom TypeScript image name
$customTsImage = "cortex-typescript-exercises:latest"

# Function to check if an image exists locally
function ImageExists {
    param (
        [string]$image
    )
    docker image inspect $image -ErrorAction SilentlyContinue
}

# Function to pull an image
function PullImage {
    param (
        [string]$image
    )
    if (ImageExists $image) {
        Write-Host "Image $image already exists locally."
    } else {
        Write-Host "Pulling image $image..."
        if (docker pull $image) {
            Write-Host "Successfully pulled $image"
        } else {
            Write-Host "Failed to pull $image"
        }
    }
}

# Function to build custom TypeScript image
function BuildCustomTsImage {
    Write-Host "Building custom TypeScript image: $customTsImage"
    
    # Navigate to the parent directory of both capstone/ and cortex-exercises/
    Set-Location (Join-Path -Path $PSScriptRoot -ChildPath "..")

    # Build the Docker image using the capstone/docker/Dockerfile
    if (docker build -t $customTsImage -f capstone/docker/Dockerfile .) {
        Write-Host "Successfully built $customTsImage"
    } else {
        Write-Host "Failed to build $customTsImage"
        exit 1
    }
}

# Main script execution
Write-Host "Starting Docker image preparation process..."

# Pull necessary base images
foreach ($image in $images) {
    PullImage $image
    Write-Host "------------------------"
}

# Build custom TypeScript image
if (ImageExists $customTsImage) {
    Write-Host "Custom TypeScript image $customTsImage already exists locally."
    $response = Read-Host "Do you want to rebuild it? (y/n)"
    if ($response -eq 'y' -or $response -eq 'Y') {
        BuildCustomTsImage
    }
} else {
    BuildCustomTsImage
}

Write-Host "Docker image preparation process completed."
