#!/bin/bash

# Array of Docker images to pull
images=(
    "python:3.12-slim"
    "maven:3.9.9-eclipse-temurin-21"
    "rust:1.80-slim"
    "mcr.microsoft.com/dotnet/sdk:8.0"
    "golang:1.23-bookworm"
)

# Custom TypeScript image name
custom_ts_image="cortex-typescript-exercises:latest"

# Function to check if an image exists locally
image_exists() {
    docker image inspect "$1" &>/dev/null
}

# Function to pull an image
pull_image() {
    if image_exists "$1"; then
        echo "Image $1 already exists locally."
    else
        echo "Pulling image $1..."
        if docker pull "$1"; then
            echo "Successfully pulled $1"
        else
            echo "Failed to pull $1"
        fi
    fi
}

# Function to build custom TypeScript image
build_custom_ts_image() {
    echo "Building custom TypeScript image: $custom_ts_image"
    
    # Navigate to the parent directory of both capstone/ and cortex-exercises/
    cd "$(dirname "$0")/.." || exit
    
    # Build the Docker image using the capstone/docker/Dockerfile
    if docker build -t "$custom_ts_image" -f capstone/docker/Dockerfile .; then
        echo "Successfully built $custom_ts_image"
    else
        echo "Failed to build $custom_ts_image"
        exit 1
    fi
}


# Main script execution
echo "Starting Docker image preparation process..."

# Pull necessary base images
for image in "${images[@]}"; do
    pull_image "$image"
    echo "------------------------"
done

# Build custom TypeScript image
if image_exists "$custom_ts_image"; then
    echo "Custom TypeScript image $custom_ts_image already exists locally."
    read -p "Do you want to rebuild it? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        build_custom_ts_image
    fi
else
    build_custom_ts_image
fi

echo "Docker image preparation process completed."