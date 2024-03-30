DROP TABLE IF EXISTS messages;

CREATE TABLE messages (
                          id SERIAL PRIMARY KEY,
                          sender_id BIGINT NOT NULL,
                          receiver_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          seen BOOLEAN NOT NULL,
                          visibility BIGINT[] NOT NULL
);
