import json
from typing import TypedDict, List, Dict, Any
import requests
import time
import logging
from rich.console import Console
from rich.logging import RichHandler
from rich.progress import Progress
from rich.panel import Panel

BASE_URL: str = "http://localhost:8088/api/v1"

console = Console()

# Configurar logging
logging.basicConfig(
    level="INFO",
    format="%(message)s",
    datefmt="[%X]",
    handlers=[RichHandler(rich_tracebacks=True)],
)

log = logging.getLogger("rich")


class User(TypedDict):
    id: int
    username: str
    email: str


class AuthResponse(TypedDict):
    token: str


class RoadmapResponse(TypedDict):
    id: int
    title: str
    description: str
    slug: str


class CourseResponse(TypedDict):
    id: int
    name: str
    description: str
    slug: str


class ModuleResponse(TypedDict):
    id: int
    name: str
    description: str
    slug: str


class LessonResponse(TypedDict):
    id: int
    name: str
    content: str
    slug: str
    credits: int


def load_data(filename: str) -> Dict[str, Any]:
    with open(filename, "r") as file:
        return json.load(file)


def register_user(user_data: Dict[str, Any]) -> None:
    url: str = f"{BASE_URL}/auth/register"
    log.info(f"Registering user: {user_data['username']}")
    try:
        response: requests.Response = requests.post(url, json=user_data)
        response.raise_for_status()
        if response.status_code == 202:
            console.print(
                f"[green]User {user_data['username']} registered successfully![/green]"
            )
        else:
            log.warning(f"Unexpected status code: {response.status_code}")
    except requests.exceptions.RequestException as e:
        log.error(f"Failed to register user: {e}")
        log.debug(f"Response content: {response.text}")
        raise


def activate_account(token: str) -> None:
    url: str = f"{BASE_URL}/auth/activate-account"
    params: Dict[str, str] = {"token": token}
    log.info("Activating account")
    try:
        response: requests.Response = requests.get(url, params=params)
        response.raise_for_status()
        console.print("[green]Account activated successfully![/green]")
    except requests.exceptions.RequestException as e:
        log.error(f"Failed to activate account: {e}")
        log.debug(f"Response content: {response.text}")
        raise


def authenticate_user(username: str, password: str) -> str:
    url: str = f"{BASE_URL}/auth/authenticate"
    data: Dict[str, str] = {"username": username, "password": password}
    log.info(f"Authenticating user: {username}")
    try:
        response: requests.Response = requests.post(url, json=data)
        response.raise_for_status()
        auth_response: AuthResponse = response.json()
        return auth_response["token"]
    except requests.exceptions.RequestException as e:
        log.error(f"Failed to authenticate user: {e}")
        log.debug(f"Response content: {response.text}")
        raise


def create_roadmap(token: str, roadmap_data: Dict[str, Any]) -> RoadmapResponse:
    url: str = f"{BASE_URL}/education/roadmap"
    headers: Dict[str, str] = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
    }
    log.info(f"Creating roadmap: {roadmap_data['title']}")
    try:
        response: requests.Response = requests.post(
            url, json=roadmap_data, headers=headers
        )
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        log.error(f"Failed to create roadmap: {e}")
        log.debug(f"Response content: {response.text}")
        raise


def create_course(token: str, course_data: Dict[str, Any]) -> CourseResponse:
    url: str = f"{BASE_URL}/education/course"
    headers: Dict[str, str] = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
    }
    log.info(f"Creating course: {course_data['name']}")
    try:
        response: requests.Response = requests.post(
            url, json=course_data, headers=headers
        )
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        log.error(f"Failed to create course: {e}")
        log.debug(f"Response content: {response.text}")
        raise


def create_module(token: str, module_data: Dict[str, Any]) -> Dict[str, Any]:
    url: str = f"{BASE_URL}/education/module"
    headers: Dict[str, str] = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
    }
    log.info(
        f"Creating module: {module_data['name']} for course ID {module_data['courseId']}"
    )
    try:
        response: requests.Response = requests.post(
            url, json=module_data, headers=headers
        )
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        log.error(f"Failed to create module: {e}")
        log.debug(f"Response content: {response.text}")
        raise


def create_lesson(token: str, lesson_data: Dict[str, Any]) -> Dict[str, Any]:
    url: str = f"{BASE_URL}/education/lesson"
    headers: Dict[str, str] = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
    }
    log.info(
        f"Creating lesson: {lesson_data['name']} for module ID {lesson_data['moduleId']}"
    )
    try:
        response: requests.Response = requests.post(
            url, json=lesson_data, headers=headers
        )
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        log.error(f"Failed to create lesson: {e}")
        log.debug(f"Response content: {response.text}")
        raise


def get_all_users(token: str) -> List[Dict[str, Any]]:
    url: str = f"{BASE_URL}/user/all"
    headers: Dict[str, str] = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
    }
    try:
        response: requests.Response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        log.error(f"Failed to get all users: {e}")
        log.debug(f"Response content: {response.text}")
        raise


def get_all_courses(token: str) -> List[Dict[str, Any]]:
    url: str = f"{BASE_URL}/education/course"
    headers: Dict[str, str] = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
    }
    try:
        response: requests.Response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        log.error(f"Failed to get all courses: {e}")
        log.debug(f"Response content: {response.text}")
        return []


def main() -> None:
    console.print(Panel.fit("Starting Cortex Data Population", style="bold magenta"))

    try:
        data: Dict[str, Any] = load_data("data.json")
        log.info("Data loaded successfully")

        # Verificar si el usuario admin ya existe
        try:
            admin_token: str = authenticate_user(
                data["admin"]["username"], data["admin"]["password"]
            )
            log.info("Admin user already exists and authenticated successfully")
        except requests.exceptions.RequestException:
            # Si la autenticación falla, asumimos que el usuario no existe y lo registramos
            register_user(data["admin"])
            console.print(
                f"[green]Admin user registered. Please check the email: {data['admin']['email']} for the activation token.[/green]"
            )

            # Esperar y solicitar el token de activación
            time.sleep(5)  # Dar tiempo para que el correo llegue
            activation_token: str = console.input(
                "[bold yellow]Please enter the activation token received in the email: [/bold yellow]"
            )

            # Activar la cuenta del admin
            activate_account(activation_token)

            # Autenticar usuario admin
            admin_token: str = authenticate_user(
                data["admin"]["username"], data["admin"]["password"]
            )

        # Obtener todos los usuarios existentes
        existing_users = get_all_users(admin_token)
        existing_usernames = {user["username"] for user in existing_users}

        # Registrar usuarios normales si no existen
        with Progress() as progress:
            task = progress.add_task(
                "[cyan]Registering users...", total=len(data["users"])
            )
            for user in data["users"]:
                if user["username"] not in existing_usernames:
                    register_user(user)
                    progress.update(task, advance=1)
                else:
                    log.info(
                        f"User {user['username']} already exists, skipping registration"
                    )
                    progress.update(task, advance=1)

        # Obtener todos los cursos existentes
        existing_courses = get_all_courses(admin_token)
        existing_course_names = {course["name"] for course in existing_courses}

        # Crear cursos
        courses: List[Dict[str, Any]] = []
        with Progress() as progress:
            task = progress.add_task(
                "[cyan]Creating courses...", total=len(data["courses"])
            )
            for course_data in data["courses"]:
                if course_data["name"] not in existing_course_names:
                    try:
                        course = create_course(admin_token, course_data)
                        courses.append(course)
                    except requests.exceptions.RequestException as e:
                        log.error(f"Failed to create course: {e}")
                else:
                    log.info(
                        f"Course '{course_data['name']}' already exists, skipping creation"
                    )
                    # Añadir el curso existente a la lista
                    existing_course = next(
                        course
                        for course in existing_courses
                        if course["name"] == course_data["name"]
                    )
                    courses.append(existing_course)
                progress.update(task, advance=1)

        # Crear módulos y lecciones para cada curso
        with Progress() as progress:
            total_modules = len(data["modules"]) * len(courses)
            total_lessons = len(data["lessons"]) * len(courses)
            task_modules = progress.add_task(
                "[cyan]Creating modules...", total=total_modules
            )
            task_lessons = progress.add_task(
                "[cyan]Creating lessons...", total=total_lessons
            )

        for course in courses:
            for module_data in data["modules"]:
                if module_data["name"] not in [m for m in course["moduleIds"]]:
                    try:
                        module_data["courseId"] = course["id"]
                        module = create_module(admin_token, module_data)
                        progress.update(task_modules, advance=1)

                        for lesson_data in data["lessons"]:
                            if lesson_data["name"] not in [
                                l for l in module["lessonIds"]
                            ]:
                                lesson_data["moduleId"] = module["id"]
                                create_lesson(admin_token, lesson_data)
                                progress.update(task_lessons, advance=1)
                            else:
                                log.info(
                                    f"Lesson '{lesson_data['name']}' already exists in module '{module['name']}', skipping creation"
                                )
                                progress.update(task_lessons, advance=1)
                    except requests.exceptions.RequestException as e:
                        log.error(f"Failed to create module or lessons: {e}")
                        progress.update(task_modules, advance=1)
                        progress.update(task_lessons, advance=len(data["lessons"]))
                else:
                    log.info(
                        f"Module '{module_data['name']}' already exists in course '{course['name']}', skipping creation"
                    )
                    progress.update(task_modules, advance=1)
                    progress.update(task_lessons, advance=len(data["lessons"]))

        # Crear roadmaps
        with Progress() as progress:
            task = progress.add_task(
                "[cyan]Creating roadmaps...", total=len(data["roadmaps"])
            )
            for roadmap_data in data["roadmaps"]:
                create_roadmap(admin_token, roadmap_data)
                progress.update(task, advance=1)

        console.print(
            Panel.fit(
                "[bold green]Data population complete![/bold green]", style="green"
            )
        )
    except Exception:
        console.print("[bold red]An error occurred during data population:[/bold red]")
        console.print_exception()


if __name__ == "__main__":
    main()
