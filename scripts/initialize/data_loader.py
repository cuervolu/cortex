import json
import os

def load_lesson_content(content_file):
  with open(os.path.join('scripts/initialize/lesson_content', content_file), 'r', encoding='utf-8') as file:
    return file.read()

def load_data(filename):
  with open(filename, 'r', encoding='utf-8') as file:
    data = json.load(file)

  for lesson in data['lessons']:
    if 'content_file' in lesson:
      lesson['content'] = load_lesson_content(lesson['content_file'])

  return data
