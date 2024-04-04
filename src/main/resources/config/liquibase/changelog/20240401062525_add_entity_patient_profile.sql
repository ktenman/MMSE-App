CREATE TABLE IF NOT EXISTS patient_profile
(
    id         BIGSERIAL PRIMARY KEY,
    patient_id VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_patient_profile_name_patient_id ON patient_profile (UPPER(name), UPPER(patient_id));

CREATE INDEX idx_patient_profile_patient_id ON patient_profile (patient_id);

ALTER TABLE test_entity
    ADD COLUMN patient_profile_id BIGINT,
    ADD CONSTRAINT fk_test_entity__patient_profile_id
        FOREIGN KEY (patient_profile_id)
            REFERENCES patient_profile (id);

ALTER TABLE test_entity
    ALTER COLUMN patient_profile_id SET NOT NULL;
