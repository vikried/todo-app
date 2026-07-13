-- Todos, die über eine Kategorie angelegt wurden, hatten kein todo_list_id
-- gesetzt (nur category_id) - CategoryService.addTodoToCategory hat das
-- versäumt. Für bestehende Daten aus der zugehörigen Kategorie nachtragen.
UPDATE todos t
SET todo_list_id = c.todo_list_id
FROM categories c
WHERE t.category_id = c.id
  AND t.todo_list_id IS NULL
  AND c.todo_list_id IS NOT NULL;
