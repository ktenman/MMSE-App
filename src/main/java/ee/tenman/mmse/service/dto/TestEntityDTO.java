package ee.tenman.mmse.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ee.tenman.mmse.domain.TestEntity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TestEntityDTO implements Serializable {

    private Long id;

    private Instant createdAt;

    private Instant updatedAt;

    private Integer score;

    private UserDTO user;

    private PatientProfileDTO patientProfile;

    private String hash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public PatientProfileDTO getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfileDTO patientProfile) {
        this.patientProfile = patientProfile;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestEntityDTO)) {
            return false;
        }

        TestEntityDTO testEntityDTO = (TestEntityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, testEntityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestEntityDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", score=" + getScore() +
            ", user=" + getUser() +
            ", patientProfile=" + getPatientProfile() +
            "}";
    }
}
