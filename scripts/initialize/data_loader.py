import json
from typing import Dict, Any

def load_data(filename: str) -> Dict[str, Any]:
  with open(filename, "r") as file:
    return json.load(file)