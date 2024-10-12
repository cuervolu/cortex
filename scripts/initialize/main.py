import logging
from rich.console import Console
from rich.logging import RichHandler
from rich.panel import Panel
from data_loader import load_data
from api_client import authenticate_user, api_request
from resource_handlers import (
    register_users,
    create_courses,
    create_modules_and_lessons,
    create_roadmaps,
    register_user,
)

IMAGE_FOLDER: str = "scripts/initialize/images"  # Ajusta esta ruta según tu estructura de directorios

console = Console()

logging.basicConfig(
    level="INFO",
    format="%(message)s",
    datefmt="[%X]",
    handlers=[RichHandler(rich_tracebacks=True)],
)

log = logging.getLogger("rich")

def main() -> None:
    console.print(Panel.fit("Starting Cortex Data Population", style="bold magenta"))

    try:
        data = load_data("scripts/initialize/data.json")
        log.info("Data loaded successfully")

        register_user(data["admin"])

        log.info("Admin user registered successfully")

        admin_token = authenticate_user(
            data["admin"]["username"], data["admin"]["password"]
        )
        log.info("Admin user authenticated successfully")

        existing_users = api_request("GET", "user/all", admin_token)
        register_users(data["users"], existing_users)

        existing_courses = api_request("GET", "education/course", admin_token)
        courses = create_courses(admin_token, data["courses"], existing_courses, IMAGE_FOLDER)

        create_modules_and_lessons(
            admin_token, data["modules"], data["lessons"], IMAGE_FOLDER
        )

        create_roadmaps(admin_token, data["roadmaps"], IMAGE_FOLDER)

        console.print(
            Panel.fit(
                "[bold green]Data population complete![/bold green]", style="green"
            )
        )
    except Exception as e:
        console.print("[bold red]An error occurred during data population:[/bold red]")
        console.print_exception()
        log.error(f"Error details: {str(e)}")

if __name__ == "__main__":
    main()