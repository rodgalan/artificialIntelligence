CREATE TABLE courses (
    Id char(4) PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    embedding vector(768)
);