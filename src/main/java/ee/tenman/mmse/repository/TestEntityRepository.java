package ee.tenman.mmse.repository;

import ee.tenman.mmse.domain.TestEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TestEntity entity.
 */
@Repository
public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
    @Query("select testEntity from TestEntity testEntity where testEntity.user.login = ?#{principal.username}")
    List<TestEntity> findByUserIsCurrentUser();

    default Optional<TestEntity> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TestEntity> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TestEntity> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select testEntity from TestEntity testEntity left join fetch testEntity.user",
        countQuery = "select count(testEntity) from TestEntity testEntity"
    )
    Page<TestEntity> findAllWithToOneRelationships(Pageable pageable);

    @Query("select testEntity from TestEntity testEntity left join fetch testEntity.user")
    List<TestEntity> findAllWithToOneRelationships();

    @Query("select testEntity from TestEntity testEntity left join fetch testEntity.user where testEntity.id =:id")
    Optional<TestEntity> findOneWithToOneRelationships(@Param("id") Long id);
}
