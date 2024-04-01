package ee.tenman.mmse.repository;

import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PatientProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    PatientProfile findByTestEntitiesContains(TestEntity testEntity);
}
