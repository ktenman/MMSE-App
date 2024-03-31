package ee.tenman.mmse.repository;

import ee.tenman.mmse.domain.DolphinQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DolphinQuestionRepository extends JpaRepository<DolphinQuestion, Long> {
    Optional<DolphinQuestion> findByQuestion(String question);
}
