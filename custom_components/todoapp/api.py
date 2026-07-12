"""Async API client for the Todo-App backend (JWT REST API)."""

from __future__ import annotations

import logging
from typing import Any

import aiohttp

_LOGGER = logging.getLogger(__name__)


class TodoAppAuthError(Exception):
    """Raised when authentication with the Todo-App backend fails."""


class TodoAppApiError(Exception):
    """Raised for any other Todo-App API error."""


class TodoAppApiClient:
    """Thin async client for the Todo-App JWT REST API."""

    def __init__(
        self,
        session: aiohttp.ClientSession,
        base_url: str,
        username: str,
        password: str,
    ) -> None:
        self._session = session
        self._base_url = base_url.rstrip("/")
        self._username = username
        self._password = password
        self._token: str | None = None

    async def async_validate_auth(self) -> None:
        """Validate credentials, raising TodoAppAuthError on failure."""
        await self._login()

    async def _login(self) -> None:
        try:
            async with self._session.post(
                f"{self._base_url}/api/auth/login",
                json={"username": self._username, "password": self._password},
            ) as resp:
                if resp.status != 200:
                    raise TodoAppAuthError(f"Anmeldung fehlgeschlagen (HTTP {resp.status})")
                data = await resp.json()
                self._token = data["token"]
        except aiohttp.ClientError as err:
            raise TodoAppApiError(f"Verbindung zum Backend fehlgeschlagen: {err}") from err

    async def _request(self, method: str, path: str, retry: bool = True, **kwargs: Any) -> Any:
        if self._token is None:
            await self._login()

        headers = {"Authorization": f"Bearer {self._token}"}
        try:
            async with self._session.request(
                method, f"{self._base_url}{path}", headers=headers, **kwargs
            ) as resp:
                if resp.status in (401, 403) and retry:
                    self._token = None
                    return await self._request(method, path, retry=False, **kwargs)
                if resp.status >= 400:
                    text = await resp.text()
                    raise TodoAppApiError(f"HTTP {resp.status} bei {path}: {text}")
                if resp.status == 204:
                    return None
                body = await resp.read()
                if not body:
                    return None
                return await resp.json()
        except aiohttp.ClientError as err:
            raise TodoAppApiError(f"Verbindung zum Backend fehlgeschlagen: {err}") from err

    async def async_get_lists(self) -> list[dict[str, Any]]:
        """Return all accessible lists plus all templates (deduplicated by id)."""
        lists = await self._request("GET", "/api/lists") or []
        templates = await self._request("GET", "/api/lists/templates") or []
        by_id: dict[int, dict[str, Any]] = {item["id"]: item for item in lists}
        for template in templates:
            by_id.setdefault(template["id"], template)
        return list(by_id.values())

    async def async_create_todo(
        self, list_id: int, title: str, category_name: str | None
    ) -> dict[str, Any]:
        payload: dict[str, Any] = {"title": title, "todoListId": list_id, "done": False}
        if category_name:
            payload["categoryName"] = category_name
        return await self._request("POST", "/api/todos", json=payload)

    async def async_update_todo(
        self,
        todo_id: int,
        *,
        title: str,
        done: bool,
        category_name: str | None,
    ) -> dict[str, Any]:
        payload: dict[str, Any] = {
            "title": title,
            "done": done,
            # Leerstring signalisiert dem Backend "Kategorie entfernen".
            "categoryName": category_name or "",
        }
        return await self._request("PATCH", f"/api/todos/{todo_id}", json=payload)

    async def async_delete_todo(self, todo_id: int) -> None:
        await self._request("DELETE", f"/api/todos/{todo_id}")
