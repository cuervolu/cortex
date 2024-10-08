import json
from typing import Dict, Any

def load_data(filename: str) -> Dict[str, Any]:
  with open(filename, "r",encoding='utf-8') as file:
    return json.load(file)