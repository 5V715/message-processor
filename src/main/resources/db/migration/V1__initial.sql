CREATE TABLE IF NOT EXISTS messages
(
    id uuid primary key,
    processed bool default false,
    body jsonb not null
);