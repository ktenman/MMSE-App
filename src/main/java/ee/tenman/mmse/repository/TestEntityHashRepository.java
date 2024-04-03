package ee.tenman.mmse.repository;

import ee.tenman.mmse.domain.TestEntityHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestEntityHashRepository extends JpaRepository<TestEntityHash, Long> {
    boolean existsByHash(String hash);

    Optional<TestEntityHash> findByHash(String hash);
}
