CREATE TABLE courses (
    id CHAR(4) PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    summary TEXT,
    categories jsonb NOT NULL,
    published_at DATE NOT NULL,
    embedding vector(768)
);