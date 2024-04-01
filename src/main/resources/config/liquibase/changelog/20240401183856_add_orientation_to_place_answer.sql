CREATE TABLE IF NOT EXISTS orientation_to_place_answer
(
    id                 BIGSERIAL PRIMARY KEY,
    question_id        VARCHAR(255) NOT NULL,
    correct_answer     VARCHAR(255) NOT NULL,
    answer_options     VARCHAR(255) NOT NULL,
    patient_profile_id BIGINT       NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_answer_option__patient_profile_id FOREIGN KEY (patient_profile_id) REFERENCES patient_profile (id),
    CONSTRAINT uq_answer_option__question_id__patient_profile_id UNIQUE (question_id, patient_profile_id)
);
