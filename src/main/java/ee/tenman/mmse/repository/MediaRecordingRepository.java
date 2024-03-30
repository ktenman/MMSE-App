package ee.tenman.mmse.repository;

import ee.tenman.mmse.domain.MediaRecording;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRecordingRepository extends JpaRepository<MediaRecording, Long> {
    Optional<MediaRecording> findFirstByTestEntityIdAndQuestionIdOrderByCreatedAtDesc(Long testEntityId, QuestionId questionId);
}
