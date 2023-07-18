package ee.tenman.mmse.repository;

import ee.tenman.mmse.domain.UserAnswer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Spring Data JPA repository for the UserAnswer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    @Query("SELECT u FROM UserAnswer u WHERE u.testEntity.id = :testEntityId ORDER BY u.createdAt DESC")
    List<UserAnswer> findLatestByTestEntityId(@Param("testEntityId") Long testEntityId, Pageable pageable);

    List<UserAnswer> findByTestEntityIdOrderByCreatedAtDesc(Long testEntityId);


}
