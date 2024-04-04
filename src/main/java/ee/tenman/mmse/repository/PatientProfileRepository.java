package ee.tenman.mmse.repository;

import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.TestEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the PatientProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    PatientProfile findByTestEntitiesContains(TestEntity testEntity);

    @Query("SELECT p FROM PatientProfile p WHERE p.patientId = :patientId AND UPPER(p.name) = UPPER(:name)")
    Optional<PatientProfile> findByPatientIdAndName(@Param("patientId") String patientId, @Param("name") String name);

    Optional<PatientProfile> findByPatientId(String patientId);

    @EntityGraph(attributePaths = {"orientationToPlaceAnswers"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<PatientProfile> findById(Long id);
}
