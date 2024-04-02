package ee.tenman.mmse.service.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link ee.tenman.mmse.domain.PatientProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PatientProfileDTO extends PatientProfileRequest implements Serializable {

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PatientProfileDTO{" +
            "id=" + getId() +
            ", patientId='" + getPatientId() + "'" +
            ", name='" + getName() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
