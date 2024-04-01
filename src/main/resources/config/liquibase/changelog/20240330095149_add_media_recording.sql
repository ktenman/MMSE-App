CREATE TABLE IF NOT EXISTS media_recording
(
    id             BIGSERIAL PRIMARY KEY,
    file_name      VARCHAR(255) NOT NULL,
    test_entity_id BIGINT       NOT NULL,
    question_id    VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_media_recording__test_entity_id FOREIGN KEY (test_entity_id) REFERENCES test_entity (id)
);
