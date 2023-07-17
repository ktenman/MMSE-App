package ee.tenman.mmse.service;

import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.dto.UserAnswerDTO;
import ee.tenman.mmse.service.mapper.UserAnswerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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

    private final UserAnswerRepository userAnswerRepository;

    private final UserAnswerMapper userAnswerMapper;

    public UserAnswerService(UserAnswerRepository userAnswerRepository, UserAnswerMapper userAnswerMapper) {
        this.userAnswerRepository = userAnswerRepository;
        this.userAnswerMapper = userAnswerMapper;
    }

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
}
