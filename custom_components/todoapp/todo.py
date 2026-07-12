"""Todo-App todo platform."""

from __future__ import annotations

from typing import Any

from homeassistant.components.todo import (
    TodoItem,
    TodoItemStatus,
    TodoListEntity,
    TodoListEntityFeature,
)
from homeassistant.core import HomeAssistant
from homeassistant.helpers.entity_platform import AddConfigEntryEntitiesCallback
from homeassistant.helpers.update_coordinator import CoordinatorEntity

from .coordinator import TodoAppConfigEntry, TodoAppCoordinator

PARALLEL_UPDATES = 0


def _format_summary(title: str, category_name: str | None) -> str:
    """"Kategorie: Titel", da HA-Todo-Listen keine Unterkategorien kennen."""
    if category_name:
        return f"{category_name}: {title}"
    return title


def _parse_summary(summary: str) -> tuple[str, str | None]:
    """Kehrt _format_summary um: "Kategorie: Titel" -> (Titel, Kategorie)."""
    if ":" in summary:
        prefix, rest = summary.split(":", 1)
        prefix = prefix.strip()
        rest = rest.strip()
        if prefix and rest:
            return rest, prefix
    return summary.strip(), None


def _todo_items_from_list(list_data: dict[str, Any]) -> list[TodoItem]:
    """Aus der Liste die Items sammeln.

    Das Backend liefert kategorisierte Todos sowohl unter categories[].todos
    als auch (durch die bidirektionale JPA-Relation) unter list.todos - hier
    per ID deduplizieren, sonst erscheint jedes kategorisierte Item doppelt.
    """
    items: list[TodoItem] = []
    seen_ids: set[int] = set()
    for category in list_data.get("categories", []):
        category_name = category.get("name")
        for todo in category.get("todos", []):
            seen_ids.add(todo["id"])
            items.append(_to_todo_item(todo, category_name))
    for todo in list_data.get("todos", []):
        if todo["id"] in seen_ids:
            continue
        items.append(_to_todo_item(todo, None))
    return items


def _to_todo_item(todo: dict[str, Any], category_name: str | None) -> TodoItem:
    return TodoItem(
        uid=str(todo["id"]),
        summary=_format_summary(todo["title"], category_name),
        status=(
            TodoItemStatus.COMPLETED if todo.get("done") else TodoItemStatus.NEEDS_ACTION
        ),
    )


async def async_setup_entry(
    hass: HomeAssistant,
    entry: TodoAppConfigEntry,
    async_add_entities: AddConfigEntryEntitiesCallback,
) -> None:
    """Set up Todo-App todo entities, inkl. später neu angelegter Listen."""
    coordinator = entry.runtime_data
    known_list_ids: set[int] = set()

    def _add_new_entities() -> None:
        new_ids = set(coordinator.data) - known_list_ids
        if not new_ids:
            return
        known_list_ids.update(new_ids)
        async_add_entities(
            TodoAppListEntity(
                coordinator, entry.entry_id, list_id, coordinator.data[list_id]["name"]
            )
            for list_id in new_ids
        )

    _add_new_entities()
    entry.async_on_unload(coordinator.async_add_listener(_add_new_entities))


class TodoAppListEntity(CoordinatorEntity[TodoAppCoordinator], TodoListEntity):
    """Eine Todo-App-Liste (oder ein Template) als HA-Todo-Entity."""

    _attr_has_entity_name = True
    _attr_supported_features = (
        TodoListEntityFeature.CREATE_TODO_ITEM
        | TodoListEntityFeature.UPDATE_TODO_ITEM
        | TodoListEntityFeature.DELETE_TODO_ITEM
    )

    def __init__(
        self,
        coordinator: TodoAppCoordinator,
        config_entry_id: str,
        list_id: int,
        name: str,
    ) -> None:
        super().__init__(coordinator)
        self._list_id = list_id
        self._attr_name = name
        self._attr_unique_id = f"{config_entry_id}-{list_id}"

    @property
    def available(self) -> bool:
        return super().available and self._list_id in self.coordinator.data

    @property
    def todo_items(self) -> list[TodoItem] | None:
        list_data = self.coordinator.data.get(self._list_id)
        if list_data is None:
            return None
        return _todo_items_from_list(list_data)

    async def async_create_todo_item(self, item: TodoItem) -> None:
        title, category_name = _parse_summary(item.summary or "")
        await self.coordinator.api.async_create_todo(self._list_id, title, category_name)
        await self.coordinator.async_request_refresh()

    async def async_update_todo_item(self, item: TodoItem) -> None:
        title, category_name = _parse_summary(item.summary or "")
        await self.coordinator.api.async_update_todo(
            int(item.uid),
            title=title,
            done=item.status == TodoItemStatus.COMPLETED,
            category_name=category_name,
        )
        await self.coordinator.async_request_refresh()

    async def async_delete_todo_items(self, uids: list[str]) -> None:
        for uid in uids:
            await self.coordinator.api.async_delete_todo(int(uid))
        await self.coordinator.async_request_refresh()
