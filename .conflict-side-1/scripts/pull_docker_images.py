import subprocess
import os
from typing import List

# Array of Docker images to pull
images: List[str] = [
    "python:3.12-slim",
    "maven:3.9.9-eclipse-temurin-21",
    "rust:1.80-slim",
    "mcr.microsoft.com/dotnet/sdk:8.0",
    "golang:1.23-bookworm",
]

# Custom TypeScript image name
custom_ts_image: str = "cortex-typescript-exercises:latest"


def image_exists(image_name: str) -> bool:
    result = subprocess.run(
        ["docker", "image", "inspect", image_name], capture_output=True, text=True
    )
    return result.returncode == 0


def pull_image(image_name: str) -> None:
    if image_exists(image_name):
        print(f"Image {image_name} already exists locally.")
    else:
        print(f"Pulling image {image_name}...")
        result = subprocess.run(
            ["docker", "pull", image_name], capture_output=True, text=True
        )
        if result.returncode == 0:
            print(f"Successfully pulled {image_name}")
        else:
            print(f"Failed to pull {image_name}")


def build_custom_ts_image() -> None:
    print(f"Building custom TypeScript image: {custom_ts_image}")

    os.chdir(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

    directory = input("Is the directory name 'capstone' or 'cortex'? ").strip().lower()
    while directory not in ["capstone", "cortex"]:
        print("Invalid input. Please enter either 'capstone' or 'cortex'.")
        directory = (
            input("Is the directory name 'capstone' or 'cortex'? ").strip().lower()
        )

    dockerfile_path = "./docker/Dockerfile"

    # Build the Docker image using the specified Dockerfile
    result = subprocess.run(
        [
            "docker",
            "build",
            "-t",
            custom_ts_image,
            "-f",
            dockerfile_path,
            ".",
        ],
        capture_output=True,
        text=True,
    )
    if result.returncode == 0:
        print(f"Successfully built {custom_ts_image}")
    else:
        print(f"Failed to build {custom_ts_image}")
        print(f"Error: {result.stderr}")
        exit(1)


def main() -> None:
    print("Starting Docker image preparation process...")

    for image in images:
        pull_image(image)
        print("------------------------")

    if image_exists(custom_ts_image):
        print(f"Custom TypeScript image {custom_ts_image} already exists locally.")
        rebuild = input("Do you want to rebuild it? (y/n) ").lower()
        if rebuild == "y":
            build_custom_ts_image()
    else:
        build_custom_ts_image()

    print("Docker image preparation process completed.")


if __name__ == "__main__":
    main()
