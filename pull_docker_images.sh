#!/bin/bash

# Array of Docker images to pull
images=(
    "python:3.12-slim"
    "maven:3.9.9-eclipse-temurin-21"
    "node:20-alpine3.19"
    "rust:1.80-slim"
    "mcr.microsoft.com/dotnet/sdk:8.0"
    "golang:1.23-bookworm"
)

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

# Main script execution
echo "Starting Docker image pull process..."

for image in "${images[@]}"; do
    pull_image "$image"
    echo "------------------------"
done

echo "Docker image pull process completed."