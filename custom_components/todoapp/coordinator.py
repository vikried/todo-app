"""DataUpdateCoordinator for the Todo-App integration."""

from __future__ import annotations

import logging
from datetime import timedelta
from typing import Any

from homeassistant.config_entries import ConfigEntry
from homeassistant.core import HomeAssistant
from homeassistant.exceptions import ConfigEntryAuthFailed
from homeassistant.helpers.update_coordinator import DataUpdateCoordinator, UpdateFailed

from .api import TodoAppApiClient, TodoAppApiError, TodoAppAuthError

_LOGGER = logging.getLogger(__name__)

# Jede Änderung über Assist (Item hinzufügen/abhaken/löschen) löst ohnehin
# sofort einen gezielten async_request_refresh() aus (siehe todo.py) - das
# reguläre Polling muss also nur Änderungen abdecken, die außerhalb von HA
# (z. B. über die Web-App) gemacht wurden. 30s erzeugte dauerhaft unnötige
# Last (jeder Zyklus lädt alle Listen/Templates inkl. aller Kategorien und
# Todos komplett neu).
UPDATE_INTERVAL = timedelta(minutes=2)

type TodoAppConfigEntry = ConfigEntry[TodoAppCoordinator]


class TodoAppCoordinator(DataUpdateCoordinator[dict[int, dict[str, Any]]]):
    """Poll all lists (inkl. Templates) und nach Listen-ID indizieren."""

    config_entry: TodoAppConfigEntry

    def __init__(
        self, hass: HomeAssistant, config_entry: TodoAppConfigEntry, api: TodoAppApiClient
    ) -> None:
        super().__init__(
            hass,
            _LOGGER,
            config_entry=config_entry,
            name="Todo-App",
            update_interval=UPDATE_INTERVAL,
        )
        self.api = api

    async def _async_update_data(self) -> dict[int, dict[str, Any]]:
        try:
            lists = await self.api.async_get_lists()
        except TodoAppAuthError as err:
            raise ConfigEntryAuthFailed(str(err)) from err
        except TodoAppApiError as err:
            raise UpdateFailed(str(err)) from err
        return {item["id"]: item for item in lists}
