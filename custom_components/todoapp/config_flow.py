"""Config flow for the Todo-App integration."""

from __future__ import annotations

import logging
from typing import Any

import voluptuous as vol

from homeassistant.config_entries import ConfigFlow, ConfigFlowResult
from homeassistant.const import CONF_PASSWORD, CONF_URL, CONF_USERNAME
from homeassistant.helpers.aiohttp_client import async_get_clientsession

from .api import TodoAppApiClient, TodoAppApiError, TodoAppAuthError
from .const import DOMAIN

_LOGGER = logging.getLogger(__name__)

DATA_SCHEMA = vol.Schema(
    {
        vol.Required(CONF_URL): str,
        vol.Required(CONF_USERNAME): str,
        vol.Required(CONF_PASSWORD): str,
    }
)


class TodoAppConfigFlow(ConfigFlow, domain=DOMAIN):
    """Handle a Todo-App config flow."""

    VERSION = 1

    async def async_step_user(
        self, user_input: dict[str, Any] | None = None
    ) -> ConfigFlowResult:
        """Handle the initial step."""
        errors: dict[str, str] = {}

        if user_input is not None:
            self._async_abort_entries_match({CONF_URL: user_input[CONF_URL]})
            session = async_get_clientsession(self.hass)
            api = TodoAppApiClient(
                session,
                user_input[CONF_URL],
                user_input[CONF_USERNAME],
                user_input[CONF_PASSWORD],
            )
            try:
                await api.async_validate_auth()
            except TodoAppAuthError:
                errors["base"] = "invalid_auth"
            except TodoAppApiError:
                _LOGGER.exception("Verbindung zur Todo-App fehlgeschlagen")
                errors["base"] = "cannot_connect"
            else:
                return self.async_create_entry(
                    title=user_input[CONF_URL],
                    data=user_input,
                )

        return self.async_show_form(step_id="user", data_schema=DATA_SCHEMA, errors=errors)
