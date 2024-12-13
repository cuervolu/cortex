import subprocess
import os
from typing import List, Tuple

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
        # Mostrar output en tiempo real para pulls también
        result = subprocess.run(["docker", "pull", image_name])
        if result.returncode == 0:
            print(f"Successfully pulled {image_name}")
        else:
            print(f"Failed to pull {image_name}")


def check_and_update_repo() -> Tuple[bool, str]:
    """
    Checks if the cortex-exercises repository is up to date and updates it if necessary.
    Returns a tuple of (success, message).
    """
    try:
        # Store current directory
        original_dir = os.getcwd()

        # Change to cortex-exercises directory
        os.chdir("cortex-exercises")

        print("Checking cortex-exercises repository status...")

        # Fetch the latest changes
        fetch_result = subprocess.run(["git", "fetch"], capture_output=True, text=True)
        if fetch_result.returncode != 0:
            return False, f"Failed to fetch: {fetch_result.stderr}"

        # Check if we're behind the remote
        status_result = subprocess.run(
            ["git", "status", "-uno"], capture_output=True, text=True
        )

        if "Your branch is behind" in status_result.stdout:
            print("Repository is outdated. Updating...")

            # Check for local changes
            diff_result = subprocess.run(
                ["git", "diff", "--quiet"], capture_output=True
            )

            if diff_result.returncode != 0:
                return (
                    False,
                    "Local changes detected. Please commit or stash them before updating.",
                )

            # Pull the latest changes
            pull_result = subprocess.run(
                ["git", "pull"], capture_output=True, text=True
            )

            if pull_result.returncode != 0:
                return False, f"Failed to pull: {pull_result.stderr}"

            print("Repository updated successfully!")
        else:
            print("Repository is up to date!")

        return True, "Repository is ready"

    except Exception as e:
        return False, f"An error occurred: {str(e)}"
    finally:
        # Return to original directory
        os.chdir(original_dir)


def build_custom_ts_image() -> None:
    print(f"Building custom TypeScript image: {custom_ts_image}")

    # Change to the parent directory of cortex
    os.chdir(
        os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    )

    print(f"Current working directory: {os.getcwd()}")

    # Check git repo status
    success, message = check_and_update_repo()
    if not success:
        print(f"Error with repository: {message}")
        exit(1)

    dockerfile_path = "./cortex/docker/typescript.Dockerfile"

    # Verify paths
    if not os.path.exists(dockerfile_path):
        print(f"Error: Dockerfile not found at {dockerfile_path}")
        exit(1)

    if not os.path.exists("./cortex-exercises"):
        print("Error: cortex-exercises directory not found")
        exit(1)

    # Ejecutar el comando docker build sin capture_output para ver el output en tiempo real
    result = subprocess.run(
        [
            "docker",
            "build",
            "--no-cache",
            "--progress", "plain",
            "-t",
            custom_ts_image,
            "-f",
            dockerfile_path,
            ".",
        ]
    )

    if result.returncode == 0:
        print(f"Successfully built {custom_ts_image}")
    else:
        print(f"Failed to build {custom_ts_image}")
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