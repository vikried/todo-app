"""The Todo-App integration."""

from __future__ import annotations

from homeassistant.const import CONF_PASSWORD, CONF_URL, CONF_USERNAME, Platform
from homeassistant.core import HomeAssistant
from homeassistant.exceptions import ConfigEntryAuthFailed, ConfigEntryNotReady
from homeassistant.helpers.aiohttp_client import async_get_clientsession

from .api import TodoAppApiClient, TodoAppApiError, TodoAppAuthError
from .coordinator import TodoAppConfigEntry, TodoAppCoordinator

PLATFORMS: list[Platform] = [Platform.TODO]


async def async_setup_entry(hass: HomeAssistant, entry: TodoAppConfigEntry) -> bool:
    """Set up Todo-App from a config entry."""
    session = async_get_clientsession(hass)
    api = TodoAppApiClient(
        session,
        entry.data[CONF_URL],
        entry.data[CONF_USERNAME],
        entry.data[CONF_PASSWORD],
    )
    try:
        await api.async_validate_auth()
    except TodoAppAuthError as err:
        raise ConfigEntryAuthFailed(str(err)) from err
    except TodoAppApiError as err:
        raise ConfigEntryNotReady(str(err)) from err

    coordinator = TodoAppCoordinator(hass, entry, api)
    await coordinator.async_config_entry_first_refresh()
    entry.runtime_data = coordinator

    await hass.config_entries.async_forward_entry_setups(entry, PLATFORMS)
    return True


async def async_unload_entry(hass: HomeAssistant, entry: TodoAppConfigEntry) -> bool:
    """Unload a config entry."""
    return await hass.config_entries.async_unload_platforms(entry, PLATFORMS)
