CREATE TABLE IF NOT EXISTS book
(
    id SERIAL PRIMARY KEY NOT NULL,
    title VARCHAR(255),
    author VARCHAR(255),
    isbn VARCHAR(255),
    year INT
);