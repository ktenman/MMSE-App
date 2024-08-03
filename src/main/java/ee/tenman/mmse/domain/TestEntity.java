package ee.tenman.mmse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * A TestEntity.
 */
@Entity
@Table(name = "test_entity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TestEntity extends BaseEntity {

    @Column(name = "score")
    private Integer score;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "testEntity")
    @JsonIgnoreProperties(value = {"testEntity"}, allowSetters = true)
    private Set<UserAnswer> userAnswers = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = {"testEntities"}, allowSetters = true)
    private PatientProfile patientProfile;

    @OneToOne(mappedBy = "testEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private TestEntityHash testEntityHash;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<UserAnswer> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(Set<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public PatientProfile getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile) {
        this.patientProfile = patientProfile;
    }

    public TestEntityHash getTestEntityHash() {
        return testEntityHash;
    }

    public void setTestEntityHash(TestEntityHash testEntityHash) {
        this.testEntityHash = testEntityHash;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((TestEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestEntity{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", score=" + getScore() +
            ", testEntityHash='" + getTestEntityHash().getHash() + "'" +
            "}";
    }
}
