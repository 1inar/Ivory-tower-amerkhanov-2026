CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(30) UNIQUE NOT NULL,
                       email VARCHAR(120) UNIQUE NOT NULL,
                       password_hash VARCHAR(100) NOT NULL,
                       role VARCHAR(10) NOT NULL DEFAULT 'POET'
                           CHECK (role IN ('POET','CRITIC','ADMIN')),
                       email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                       bio TEXT,
                       created_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE TABLE email_tokens (
                              id BIGSERIAL PRIMARY KEY,
                              user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              token VARCHAR(64) UNIQUE NOT NULL,
                              expires_at TIMESTAMP NOT NULL
);
CREATE TABLE poems (
                       id BIGSERIAL PRIMARY KEY,
                       author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       title VARCHAR(120) NOT NULL,
                       body TEXT NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT now(),
                       updated_at TIMESTAMP
);
CREATE INDEX idx_poems_author ON poems(author_id);
CREATE TABLE tags (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(30) UNIQUE NOT NULL
);
CREATE TABLE poem_tags (
                           poem_id BIGINT REFERENCES poems(id) ON DELETE CASCADE,
                           tag_id INT REFERENCES tags(id) ON DELETE CASCADE,
                           PRIMARY KEY (poem_id, tag_id)
);
CREATE TABLE poem_likes (
                            user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                            poem_id BIGINT REFERENCES poems(id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, poem_id)
);
CREATE TABLE favorites (
                           user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                           poem_id BIGINT REFERENCES poems(id) ON DELETE CASCADE,
                           PRIMARY KEY (user_id, poem_id)
);
CREATE TABLE reviews (
                         id BIGSERIAL PRIMARY KEY,
                         poem_id BIGINT NOT NULL REFERENCES poems(id) ON DELETE CASCADE,
                         critic_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         score SMALLINT NOT NULL CHECK (score BETWEEN 1 AND 10),
                         text TEXT NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT now(),
                         UNIQUE (poem_id, critic_id)
);
CREATE TABLE highlights (
                            user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                            poem_id BIGINT REFERENCES poems(id) ON DELETE CASCADE,
                            line_no INT NOT NULL CHECK (line_no > 0),
                            PRIMARY KEY (user_id, poem_id, line_no)
);
CREATE INDEX idx_highlights_poem ON highlights(poem_id);

ALTER TABLE users ADD COLUMN critic_blocked BOOLEAN NOT NULL DEFAULT FALSE;