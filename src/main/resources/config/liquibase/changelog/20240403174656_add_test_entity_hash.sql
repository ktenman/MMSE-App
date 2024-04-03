CREATE TABLE IF NOT EXISTS test_entity_hash
(
    id             BIGSERIAL PRIMARY KEY,
    hash           VARCHAR(8)  NOT NULL UNIQUE,
    test_entity_id BIGINT      NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_test_entity_hash__test_entity_id FOREIGN KEY (test_entity_id) REFERENCES test_entity (id)
);
