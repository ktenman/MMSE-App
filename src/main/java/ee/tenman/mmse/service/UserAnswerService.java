package ee.tenman.mmse.service;

import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.User;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.repository.TestEntityRepository;
import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.dto.UserAnswerDTO;
import ee.tenman.mmse.service.mapper.UserAnswerMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link UserAnswer}.
 */
@Service
@Transactional
public class UserAnswerService {

    private final Logger log = LoggerFactory.getLogger(UserAnswerService.class);

    @Resource
    private UserAnswerRepository userAnswerRepository;
    @Resource
    private UserAnswerMapper userAnswerMapper;
    @Resource
    private TestEntityRepository testEntityRepository;
    @Resource
    private UserService userService;

    /**
     * Save a userAnswer.
     *
     * @param userAnswerDTO the entity to save.
     * @return the persisted entity.
     */
    public UserAnswerDTO save(UserAnswerDTO userAnswerDTO) {
        log.debug("Request to save UserAnswer : {}", userAnswerDTO);
        UserAnswer userAnswer = userAnswerMapper.toEntity(userAnswerDTO);
        userAnswer = userAnswerRepository.save(userAnswer);
        return userAnswerMapper.toDto(userAnswer);
    }

    /**
     * Update a userAnswer.
     *
     * @param userAnswerDTO the entity to save.
     * @return the persisted entity.
     */
    public UserAnswerDTO update(UserAnswerDTO userAnswerDTO) {
        log.debug("Request to update UserAnswer : {}", userAnswerDTO);
        UserAnswer userAnswer = userAnswerMapper.toEntity(userAnswerDTO);
        userAnswer = userAnswerRepository.save(userAnswer);
        return userAnswerMapper.toDto(userAnswer);
    }

    /**
     * Partially update a userAnswer.
     *
     * @param userAnswerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserAnswerDTO> partialUpdate(UserAnswerDTO userAnswerDTO) {
        log.debug("Request to partially update UserAnswer : {}", userAnswerDTO);

        return userAnswerRepository
            .findById(userAnswerDTO.getId())
            .map(existingUserAnswer -> {
                userAnswerMapper.partialUpdate(existingUserAnswer, userAnswerDTO);

                return existingUserAnswer;
            })
            .map(userAnswerRepository::save)
            .map(userAnswerMapper::toDto);
    }

    /**
     * Get all the userAnswers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserAnswerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserAnswers");
        return userAnswerRepository.findAll(pageable).map(userAnswerMapper::toDto);
    }

    /**
     * Get one userAnswer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserAnswerDTO> findOne(Long id) {
        log.debug("Request to get UserAnswer : {}", id);
        return userAnswerRepository.findById(id).map(userAnswerMapper::toDto);
    }

    /**
     * Delete the userAnswer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UserAnswer : {}", id);
        userAnswerRepository.deleteById(id);
    }

    public Optional<UserAnswer> getLatest() {
        Optional<User> user = userService.findUserWithAuthorities();
        if (user.isEmpty()) {
            throw new RuntimeException("No logged in user found");
        }
        Long userId = user.get().getId();
        Optional<TestEntity> latestTestEntity = testEntityRepository.findLatestByUserId(userId, PageRequest.of(0, 1)).stream().findFirst();

        if (latestTestEntity.isEmpty()) {
            return Optional.empty();
        }

        return latestTestEntity.flatMap(testEntity ->
            userAnswerRepository.findLatestByTestEntityId(testEntity.getId(), PageRequest.of(0, 1)).stream().findFirst()
        );
    }

}
