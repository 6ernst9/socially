DROP TABLE IF EXISTS session;

CREATE TABLE session (
                          id SERIAL PRIMARY KEY,
                          user_id BIGINT,
                          device_id VARCHAR(255) NOT NULL,
                          site_id VARCHAR(255) NOT NULL,
                          ip_address VARCHAR(255) NOT NULL
);
