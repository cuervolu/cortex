from typing import List, Dict, Any
from rich.progress import Progress
from rich.console import Console
from api_client import api_request
import logging

log = logging.getLogger("rich")
console = Console()


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


def create_resource(
    resource_type: str, token: str, data: Dict[str, Any]
) -> Dict[str, Any]:
    key = "title" if resource_type == "roadmap" else "name"
    resource_name = data.get(key, "Unnamed")
    log.info(f"Creating {resource_type}: {resource_name}")
    try:
        response = api_request("POST", f"education/{resource_type}", token, data)
        if response is None:
            log.warning(
                f"{resource_type.capitalize()} '{resource_name}' may already exist"
            )
            return data
        return response
    except Exception as e:
        log.error(f"Failed to create {resource_type} '{resource_name}': {str(e)}")
        log.debug(f"Data used for creation: {data}")
        raise


def create_resources_with_progress(
    resource_type: str,
    token: str,
    resources_data: List[Dict[str, Any]],
    existing_resources: List[Dict[str, Any]],
) -> List[Dict[str, Any]]:
    # Use 'title' for roadmaps, 'name' for other resources
    key = "title" if resource_type == "roadmap" else "name"
    existing_names = {resource.get(key) for resource in existing_resources}
    created_resources = []

    with Progress() as progress:
        task = progress.add_task(
            f"[cyan]Creating {resource_type}s...", total=len(resources_data)
        )
        for resource_data in resources_data:
            if resource_data.get(key) not in existing_names:
                try:
                    resource = create_resource(resource_type, token, resource_data)
                    created_resources.append(resource)
                except Exception as e:
                    log.error(f"Failed to create {resource_type}: {e}")
            else:
                log.info(
                    f"{resource_type.capitalize()} '{resource_data.get(key)}' already exists, skipping creation"
                )
                existing_resource = next(
                    resource
                    for resource in existing_resources
                    if resource.get(key) == resource_data.get(key)
                )
                created_resources.append(existing_resource)
            progress.update(task, advance=1)

    return created_resources


def create_courses(
    token: str,
    courses_data: List[Dict[str, Any]],
    existing_courses: List[Dict[str, Any]],
) -> List[Dict[str, Any]]:
    return create_resources_with_progress(
        "course", token, courses_data, existing_courses
    )


def create_modules_and_lessons(
    token: str,
    courses: List[Dict[str, Any]],
    modules_data: List[Dict[str, Any]],
    lessons_data: List[Dict[str, Any]],
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
                module = create_resource("module", token, module_data)
                created_modules[module_data["courseId"]] = module
                progress.update(task_modules, advance=1)
            except Exception as e:
                log.error(f"Failed to create module '{module_data['name']}': {e}")

        for lesson_data in lessons_data:
            try:
                module_id = created_modules.get(lesson_data["moduleId"], {}).get("id")
                if module_id:
                    lesson_data["moduleId"] = module_id
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


def create_roadmaps(token: str, roadmaps_data: List[Dict[str, Any]]) -> None:
    create_resources_with_progress("roadmap", token, roadmaps_data, [])
