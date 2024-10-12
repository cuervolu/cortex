from typing import Dict, Any, Union
import requests
import logging

BASE_URL: str = "http://localhost:8080/api/v1"

log = logging.getLogger("rich")


def api_request(
    method: str, endpoint: str, token: str = "", data: Dict[str, Any] = None
) -> Union[Dict[str, Any], None]:
    url: str = f"{BASE_URL}/{endpoint}"
    headers: Dict[str, str] = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"

    try:
        response: requests.Response = requests.request(
            method, url, json=data, headers=headers
        )
        if response.status_code == 409:
            log.warning(
                f"Resource conflict (409) for endpoint {endpoint}: {response.text}"
            )
            return None
        response.raise_for_status()
        if response.status_code == 202:
            log.info(f"Request accepted: {response.status_code}")
            return None
        return response.json() if response.text else None
    except requests.exceptions.RequestException as e:
        log.error(f"API request failed for endpoint {endpoint}: {e}")
        log.debug(f"Response status code: {response.status_code}")
        log.debug(f"Response content: {response.text}")
        raise


def authenticate_user(username: str, password: str) -> str:
    log.info(f"Authenticating user: {username}")
    auth_response = api_request(
        "POST", "auth/authenticate", data={"username": username, "password": password}
    )
    if auth_response and "token" in auth_response:
        return auth_response["token"]
    else:
        raise ValueError("Authentication failed: No token received")
