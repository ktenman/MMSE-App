package ee.tenman.mmse.service;

import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.User;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.repository.TestEntityRepository;
import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.dto.TestEntityDTO;
import ee.tenman.mmse.service.mapper.TestEntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link TestEntity}.
 */
@Service
@Transactional
public class TestEntityService {

    private final Logger log = LoggerFactory.getLogger(TestEntityService.class);

    private final TestEntityRepository testEntityRepository;

    private final UserAnswerRepository userAnswerRepository;

    private final TestEntityMapper testEntityMapper;

    private final UserService userService;

    private final PatientProfileService patientProfileService;

    public TestEntityService(
        TestEntityRepository testEntityRepository,
        TestEntityMapper testEntityMapper,
        UserAnswerRepository userAnswerRepository, UserService userService, PatientProfileService patientProfileService
    ) {
        this.testEntityRepository = testEntityRepository;
        this.testEntityMapper = testEntityMapper;
        this.userAnswerRepository = userAnswerRepository;
        this.userService = userService;
        this.patientProfileService = patientProfileService;
    }

    /**
     * Save a testEntity.
     *
     * @param testEntityDTO the entity to save.
     * @return the persisted entity.
     */
    public TestEntityDTO save(TestEntityDTO testEntityDTO) {
        log.debug("Request to save TestEntity : {}", testEntityDTO);
        TestEntity testEntity = testEntityMapper.toEntity(testEntityDTO);
        testEntity = testEntityRepository.save(testEntity);
        return testEntityMapper.toDto(testEntity);
    }

    /**
     * Update a testEntity.
     *
     * @param testEntityDTO the entity to save.
     * @return the persisted entity.
     */
    public TestEntityDTO update(TestEntityDTO testEntityDTO) {
        log.debug("Request to update TestEntity : {}", testEntityDTO);
        TestEntity testEntity = testEntityMapper.toEntity(testEntityDTO);
        testEntity = testEntityRepository.save(testEntity);
        return testEntityMapper.toDto(testEntity);
    }

    /**
     * Partially update a testEntity.
     *
     * @param testEntityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TestEntityDTO> partialUpdate(TestEntityDTO testEntityDTO) {
        log.debug("Request to partially update TestEntity : {}", testEntityDTO);

        return testEntityRepository
            .findById(testEntityDTO.getId())
            .map(existingTestEntity -> {
                testEntityMapper.partialUpdate(existingTestEntity, testEntityDTO);

                return existingTestEntity;
            })
            .map(testEntityRepository::save)
            .map(testEntityMapper::toDto);
    }

    /**
     * Get all the testEntities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TestEntityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TestEntities");
        return testEntityRepository.findAll(pageable).map(testEntityMapper::toDto);
    }

    /**
     * Get all the testEntities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TestEntityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return testEntityRepository.findAllWithEagerRelationships(pageable).map(testEntityMapper::toDto);
    }

    /**
     * Get one testEntity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TestEntityDTO> findOne(Long id) {
        log.debug("Request to get TestEntity : {}", id);
        return testEntityRepository.findOneWithEagerRelationships(id).map(testEntityMapper::toDto);
    }

    /**
     * Delete the testEntity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        List<UserAnswer> answers = userAnswerRepository.findByTestEntityIdOrderByCreatedAtDesc(id);
        userAnswerRepository.deleteAll(answers);
        log.debug("Request to delete TestEntity : {}", id);
        testEntityRepository.deleteById(id);
    }

    public TestEntity getLast() {
        User user = userService.getUserWithAuthorities();
        return testEntityRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).orElseGet(() -> {
            PatientProfile patientProfile = new PatientProfile();
            patientProfile.setPatientId("123");
            patientProfile.setName("John Doe");
            PatientProfile savePatientProfile = patientProfileService.save(patientProfile);
            TestEntity t = new TestEntity();
            t.setUser(user);
            t.setPatientProfile(savePatientProfile);
            return testEntityRepository.save(t);
        });
    }

    public void save(TestEntity testEntity) {
        testEntityRepository.save(testEntity);
    }
}
