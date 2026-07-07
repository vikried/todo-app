CREATE TABLE users (
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE todo_lists (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255),
    template BOOLEAN NOT NULL DEFAULT FALSE,
    user_id  BIGINT REFERENCES users (id)
);

CREATE TABLE categories (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255),
    todo_list_id BIGINT REFERENCES todo_lists (id)
);

CREATE TABLE todos (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255),
    done         BOOLEAN,
    todo_list_id BIGINT REFERENCES todo_lists (id),
    category_id  BIGINT REFERENCES categories (id)
);
