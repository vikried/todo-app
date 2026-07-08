CREATE TABLE todo_list_shares (
    todo_list_id BIGINT NOT NULL REFERENCES todo_lists (id) ON DELETE CASCADE,
    user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (todo_list_id, user_id)
);
