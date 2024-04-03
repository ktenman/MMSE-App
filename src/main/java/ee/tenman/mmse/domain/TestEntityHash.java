package ee.tenman.mmse.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_entity_hash")
public class TestEntityHash extends BaseEntity {

    @Column(name = "hash", unique = true, nullable = false)
    private String hash;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_entity_id", nullable = false)
    private TestEntity testEntity;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public TestEntity getTestEntity() {
        return testEntity;
    }

    public void setTestEntity(TestEntity testEntity) {
        this.testEntity = testEntity;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestEntityHash)) {
            return false;
        }
        return getId() != null && getId().equals(((TestEntityHash) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestEntityHash{" +
            "id=" + getId() +
            ", hash='" + getHash() + "'" +
            ", testEntityId='" + getTestEntity().getId() + "'" +
            "}";
    }
}
