CREATE TABLE media_recording
(
    id             BIGSERIAL PRIMARY KEY,
    file_name      VARCHAR(255) NOT NULL,
    test_entity_id BIGINT       NOT NULL,
    question_id    VARCHAR(255) NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL,
    CONSTRAINT fk_audio_recording__test_entity_id FOREIGN KEY (test_entity_id) REFERENCES test_entity (id)
);
