import subprocess
import os
from typing import Optional


def run_command(command: str) -> int:
    process: subprocess.Popen[str] = subprocess.Popen(
        command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, shell=True, text=True
    )
    while True:
        output: Optional[str] = process.stdout.readline()
        if output == "" and process.poll() is not None:
            break
        if output:
            print(output.strip())
    return process.poll() or 0


def change_directory(path: str) -> None:
    os.chdir(path)
    print(f"Cambiado al directorio: {os.getcwd()}")


def main() -> None:
    print("Cambiando de directorio para el docker build...")
    change_directory("apps/backend")

    print("Ejecutando docker build...")
    build_command: str = "docker build -t cortex-backend -f Dockerfile ."
    build_result: int = run_command(build_command)

    if build_result == 0:
        print("Docker build completado con éxito.")

        print("Cambiando de directorio y ejecutando docker compose...")
        change_directory("..")
        change_directory("..")
        compose_command: str = "docker compose up -d"
        compose_result: int = run_command(compose_command)

        if compose_result == 0:
            print("Docker compose ejecutado con éxito.")
        else:
            print("Error al ejecutar docker compose.")
    else:
        print("Error en el proceso de docker build.")


if __name__ == "__main__":
    main()
