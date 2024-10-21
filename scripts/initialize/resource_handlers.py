import mimetypes
import os
import re
from pathlib import Path
from typing import List, Dict, Any, Union, Optional

import requests
from rich.progress import Progress
from rich.console import Console
from api_client import api_request, BASE_URL
import logging
from slugify import slugify

log = logging.getLogger("rich")
console = Console()


def get_image_extension(image_path: Optional[str]) -> str:
    if not image_path:
        return ".png"

    path = Path(image_path)
    mime_type, _ = mimetypes.guess_type(image_path)

    mime_to_ext = {
        "image/jpeg": ".jpg",
        "image/jpg": ".jpg",
        "image/png": ".png",
        "image/webp": ".webp",
    }

    if mime_type in mime_to_ext:
        return mime_to_ext[mime_type]

    return path.suffix if path.suffix else ".png"


def generate_valid_filename(
    resource_name: str, image_path: Optional[str] = None
) -> str:
    slug = slugify(resource_name, separator="-", lowercase=True)

    extension = get_image_extension(image_path)

    if not slug:
        # This should never happen, but just in case
        slug = "unnamed-resource"

    return f"{slug}{extension}"


def register_user(user_data: Dict[str, Any]) -> None:
    log.info(f"Registering user: {user_data['username']}")
    try:
        response = api_request("POST", "auth/register", data=user_data)
        if response is None:  # This indicates a 202 response
            console.print(
                f"[green]User {user_data['username']} registration accepted![/green]"
            )
        else:
            log.warning(f"Unexpected response format: {response}")
            console.print(
                f"[yellow]Warning: Unexpected response when registering {user_data['username']}[/yellow]"
            )
    except Exception as e:
        log.error(f"Error registering user {user_data['username']}: {str(e)}")
        console.print(
            f"[red]Error: Failed to register user {user_data['username']}[/red]"
        )


def register_users(
    users_data: List[Dict[str, Any]], existing_users: List[Dict[str, Any]]
) -> None:
    existing_usernames = {user["username"] for user in existing_users}
    with Progress() as progress:
        task = progress.add_task("[cyan]Registering users...", total=len(users_data))
        for user in users_data:
            if user["username"] not in existing_usernames:
                register_user(user)
            else:
                log.info(
                    f"User {user['username']} already exists, skipping registration"
                )
            progress.update(task, advance=1)


def get_content_type(file_path: str) -> str:
    mime_type, _ = mimetypes.guess_type(file_path)
    if mime_type in ["image/jpeg", "image/png", "image/webp"]:
        return mime_type
    return "application/octet-stream"


def upload_image(
    resource_type: str, resource_id: int, image_path: str, token: str
) -> None:
    endpoint = f"education/{resource_type}/{resource_id}/image"
    content_type = get_content_type(image_path)

    with open(image_path, "rb") as image_file:
        files = {"image": (os.path.basename(image_path), image_file, content_type)}
        data = {"altText": os.path.splitext(os.path.basename(image_path))[0]}

        try:
            response = requests.post(
                f"{BASE_URL}/{endpoint}",
                files=files,
                data=data,
                headers={"Authorization": f"Bearer {token}"},
            )
            response.raise_for_status()
            log.info(f"Image uploaded successfully for {resource_type} {resource_id}")
        except requests.exceptions.RequestException as e:
            log.error(
                f"Failed to upload image for {resource_type} {resource_id}: {str(e)}"
            )
            if response.status_code == 400:
                log.error(f"Server response: {response.text}")


def create_resource(
    resource_type: str,
    token: str,
    data: Dict[str, Any],
    image_folder: str = None
) -> Dict[str, Any]:
    key = "title" if resource_type == "roadmap" else "name"
    resource_name = data.get(key, "Unnamed")
    log.info(f"Creating {resource_type}: {resource_name}")

    try:
        response = api_request("POST", f"education/{resource_type}", token, data)
        if response is None:
            log.warning(f"{resource_type.capitalize()} '{resource_name}' may already exist")
            return data

<<<<<<< ours
        if image_folder and "id" in response:
            possible_extensions = [".png", ".jpg", ".jpeg", ".webp"]
            image_path = None

            # Primero intentamos encontrar el archivo exacto
            original_filename = generate_valid_filename(resource_name)
            full_path = os.path.join(image_folder, original_filename)
            if os.path.exists(full_path):
                image_path = full_path
            else:
                base_name = os.path.splitext(original_filename)[0]
                for ext in possible_extensions:
                    test_path = os.path.join(image_folder, f"{base_name}{ext}")
                    if os.path.exists(test_path):
                        image_path = test_path
                        break

            if image_path:
                final_filename = generate_valid_filename(resource_name, image_path)
                final_path = os.path.join(image_folder, final_filename)
                if image_path != final_path:
                    os.rename(image_path, final_path)
                    image_path = final_path

                upload_image(resource_type, response["id"], image_path, token)
||||||| ancestor
        if image_folder and 'id' in response:
            image_filename = generate_valid_filename(resource_name)
            image_path = os.path.join(image_folder, image_filename)
            if os.path.exists(image_path):
                upload_image(resource_type, response['id'], image_path, token)
=======
        if image_folder and "id" in response:
            image_filename = generate_valid_filename(resource_name)
            image_path = os.path.join(image_folder, image_filename)
            if os.path.exists(image_path):
                upload_image(resource_type, response["id"], image_path, token)
>>>>>>> theirs
            else:
                log.warning(f"No image found for {resource_type}: {resource_name}")

        return response
    except Exception as e:
        log.error(f"Failed to create {resource_type} '{resource_name}': {str(e)}")
        log.debug(f"Data used for creation: {data}")
        raise


def create_resources_with_progress(
    resource_type: str,
    token: str,
    resources_data: List[Dict[str, Any]],
    existing_resources: Union[List[Dict[str, Any]], Dict[str, Any]],
    image_folder: str = None,
) -> List[Dict[str, Any]]:
    # Use 'title' for roadmaps, 'name' for other resources
    key = "title" if resource_type == "roadmap" else "name"

    # Handle different types of existing_resources
    if isinstance(existing_resources, dict):
        content = existing_resources.get("content", [])
    elif isinstance(existing_resources, list):
        content = existing_resources
    else:
        content = []

    existing_names = set()
    for resource in content:
        if isinstance(resource, dict):
            existing_names.add(resource.get(key))
        else:
            log.error(f"Invalid resource in existing_resources: {resource}")

    created_resources = []

    with Progress() as progress:
        task = progress.add_task(
            f"[cyan]Creating {resource_type}s...", total=len(resources_data)
        )
        for resource_data in resources_data:
            if not isinstance(resource_data, dict):
                log.error(f"Invalid resource data: {resource_data}")
                progress.update(task, advance=1)
                continue

            if resource_data.get(key) not in existing_names:
                try:
                    resource = create_resource(
                        resource_type, token, resource_data, image_folder
                    )
                    if resource is not None:
                        created_resources.append(resource)
                    else:
                        log.info(
                            f"{resource_type.capitalize()} '{resource_data.get(key)}' already exists, skipping creation"
                        )
                except Exception as e:
                    log.error(f"Failed to create {resource_type}: {e}")
            else:
                log.info(
                    f"{resource_type.capitalize()} '{resource_data.get(key)}' already exists, skipping creation"
                )
                existing_resource = next(
                    (
                        resource
                        for resource in content
                        if isinstance(resource, dict)
                        and resource.get(key) == resource_data.get(key)
                    ),
                    None,
                )
                if existing_resource:
                    created_resources.append(existing_resource)
                else:
                    log.warning(
                        f"Couldn't find existing resource for '{resource_data.get(key)}'"
                    )
            progress.update(task, advance=1)

    return created_resources


def create_courses(
    token: str,
    courses_data: List[Dict[str, Any]],
    existing_courses: Union[List[Dict[str, Any]], Dict[str, Any]],
    image_folder: str = None,
) -> List[Dict[str, Any]]:
    return create_resources_with_progress(
        "course", token, courses_data, existing_courses, image_folder
    )


def create_roadmaps(
    token: str, roadmaps_data: List[Dict[str, Any]], image_folder: str = None
) -> None:
    create_resources_with_progress("roadmap", token, roadmaps_data, [], image_folder)


def create_modules_and_lessons(
    token: str,
    modules_data: List[Dict[str, Any]],
    lessons_data: List[Dict[str, Any]],
    image_folder: str = None,
) -> None:
    with Progress() as progress:
        total_modules = len(modules_data)
        total_lessons = len(lessons_data)
        task_modules = progress.add_task(
            "[cyan]Creating modules...", total=total_modules
        )
        task_lessons = progress.add_task(
            "[cyan]Creating lessons...", total=total_lessons
        )

        created_modules = {}  # To keep track of created modules by courseId

        for module_data in modules_data:
            try:
                module = create_resource("module", token, module_data, image_folder)
                created_modules[module_data["course_id"]] = module
                progress.update(task_modules, advance=1)
            except Exception as e:
                log.error(f"Failed to create module '{module_data['name']}': {e}")

        for lesson_data in lessons_data:
            try:
                module_id = created_modules.get(lesson_data["module_id"], {}).get("id")
                if module_id:
                    lesson_data["module_id"] = module_id
                    create_resource("lesson", token, lesson_data)
                    progress.update(task_lessons, advance=1)
                else:
                    log.warning(
                        f"Module not found for lesson '{lesson_data['name']}', skipping"
                    )
            except Exception as e:
                log.error(f"Failed to create lesson '{lesson_data['name']}': {e}")

        # Update progress for any remaining modules or lessons (in case of errors)
        progress.update(task_modules, completed=total_modules)
        progress.update(task_lessons, completed=total_lessons)
