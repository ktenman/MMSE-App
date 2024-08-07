package ee.tenman.mmse.repository;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the UserAnswer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    @Query("SELECT u FROM UserAnswer u WHERE u.testEntity.id = :testEntityId ORDER BY u.createdAt DESC")
    List<UserAnswer> findLatestByTestEntityId(@Param("testEntityId") Long testEntityId, Pageable pageable);

    List<UserAnswer> findByTestEntityIdOrderByCreatedAtDesc(Long testEntityId);

    @Query("SELECT u FROM UserAnswer u WHERE u.score is null ORDER BY u.createdAt ASC")
    List<UserAnswer> findUnscored();

    Optional<UserAnswer> findFirstByTestEntityIdAndQuestionIdOrderByCreatedAtDesc(Long testEntityId, QuestionId questionId);

}
