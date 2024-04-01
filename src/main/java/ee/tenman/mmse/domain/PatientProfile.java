package ee.tenman.mmse.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patient_profile")
public class PatientProfile extends BaseEntity {

    @NotNull
    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patientProfile")
    private Set<TestEntity> testEntities = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patientProfile")
    private Set<OrientationToPlaceAnswer> orientationToPlaceAnswers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TestEntity> getTestEntities() {
        return testEntities;
    }

    public void setTestEntities(Set<TestEntity> testEntities) {
        this.testEntities = testEntities;
    }

    public Set<OrientationToPlaceAnswer> getOrientationToPlaceAnswers() {
        return orientationToPlaceAnswers;
    }

    public void setOrientationToPlaceAnswers(Set<OrientationToPlaceAnswer> orientationToPlaceAnswers) {
        this.orientationToPlaceAnswers = orientationToPlaceAnswers;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatientProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((PatientProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PatientProfile{" +
            "id=" + getId() +
            ", patientId='" + getPatientId() + "'" +
            ", name='" + getName() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
