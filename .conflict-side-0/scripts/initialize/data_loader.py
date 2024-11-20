import json
import logging
import os
from typing import Dict, Any

log = logging.getLogger("rich")


def load_lesson_content(
    content_file: str, base_path: str = "scripts/initialize/lesson_content"
) -> str:
    file_path = os.path.join(base_path, content_file)
    try:
        if not os.path.exists(file_path):
            log.error(f"Lesson content file not found: {file_path}")
            return f"# Content Not Found\nThe content for {content_file} could not be loaded."

        with open(file_path, "r", encoding="utf-8") as file:
            return file.read()
    except Exception as e:
        log.error(f"Error loading lesson content from {file_path}: {str(e)}")
        return f"# Error Loading Content\nThere was an error loading the content for {content_file}"


def load_data(filename: str) -> Dict[str, Any]:
    """
    Load and transform the complete dataset
    """
    try:
        with open(filename, "r", encoding="utf-8") as file:
            data = json.load(file)

        transformed_lessons = []
        for lesson in data["lessons"]:
            new_lesson = lesson.copy()
            if "content_file" in new_lesson:
                content = load_lesson_content(new_lesson["content_file"])
                new_lesson["content"] = content
                del new_lesson["content_file"]

            transformed_lessons.append(new_lesson)

        data["lessons"] = transformed_lessons
        for lesson in data["lessons"]:
            if "content_file" in lesson:
                log.warning(
                    f"Lesson still has content_file after transformation: {lesson.get('name', 'unnamed')}"
                )
            if "content" not in lesson:
                log.warning(
                    f"Lesson missing content after transformation: {lesson.get('name', 'unnamed')}"
                )

        return data
    except Exception as e:
        log.error(f"Error loading data from {filename}: {str(e)}")
        raise
