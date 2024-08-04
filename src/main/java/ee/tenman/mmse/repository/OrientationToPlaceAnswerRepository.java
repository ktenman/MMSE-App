package ee.tenman.mmse.repository;

import ee.tenman.mmse.domain.OrientationToPlaceAnswer;
import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the PatientProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrientationToPlaceAnswerRepository extends JpaRepository<OrientationToPlaceAnswer, Long> {
    Optional<OrientationToPlaceAnswer> findByPatientProfileAndQuestionId(PatientProfile patientProfile, QuestionId questionId);

    Optional<OrientationToPlaceAnswer> findByQuestionIdAndPatientProfileId(QuestionId questionId, Long id);
}
