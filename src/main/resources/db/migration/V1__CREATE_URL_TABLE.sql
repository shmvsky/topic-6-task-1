CREATE TABLE url
(
    id              BIGSERIAL PRIMARY KEY,
    destination    VARCHAR(500) NOT NULL,
    short_url       VARCHAR(255) UNIQUE NOT NULL,
    ttl TIMESTAMP WITHOUT TIME ZONE
);
