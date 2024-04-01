package ee.tenman.mmse.service;

import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.repository.PatientProfileRepository;
import ee.tenman.mmse.service.dto.PatientProfileDTO;
import ee.tenman.mmse.service.mapper.PatientProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link ee.tenman.mmse.domain.PatientProfile}.
 */
@Service
@Transactional
public class PatientProfileService {

    private final Logger log = LoggerFactory.getLogger(PatientProfileService.class);

    private final PatientProfileRepository patientProfileRepository;

    private final PatientProfileMapper patientProfileMapper;

    public PatientProfileService(PatientProfileRepository patientProfileRepository, PatientProfileMapper patientProfileMapper) {
        this.patientProfileRepository = patientProfileRepository;
        this.patientProfileMapper = patientProfileMapper;
    }

    /**
     * Save a patientProfile.
     *
     * @param patientProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public PatientProfileDTO save(PatientProfileDTO patientProfileDTO) {
        log.debug("Request to save PatientProfile : {}", patientProfileDTO);
        PatientProfile patientProfile = patientProfileMapper.toEntity(patientProfileDTO);
        patientProfile = patientProfileRepository.save(patientProfile);
        return patientProfileMapper.toDto(patientProfile);
    }

    public PatientProfile save(PatientProfile patientProfile) {
        return patientProfileRepository.save(patientProfile);
    }

    /**
     * Update a patientProfile.
     *
     * @param patientProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public PatientProfileDTO update(PatientProfileDTO patientProfileDTO) {
        log.debug("Request to update PatientProfile : {}", patientProfileDTO);
        PatientProfile patientProfile = patientProfileMapper.toEntity(patientProfileDTO);
        patientProfile = patientProfileRepository.save(patientProfile);
        return patientProfileMapper.toDto(patientProfile);
    }

    /**
     * Partially update a patientProfile.
     *
     * @param patientProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PatientProfileDTO> partialUpdate(PatientProfileDTO patientProfileDTO) {
        log.debug("Request to partially update PatientProfile : {}", patientProfileDTO);

        return patientProfileRepository
            .findById(patientProfileDTO.getId())
            .map(existingPatientProfile -> {
                patientProfileMapper.partialUpdate(existingPatientProfile, patientProfileDTO);

                return existingPatientProfile;
            })
            .map(patientProfileRepository::save)
            .map(patientProfileMapper::toDto);
    }

    /**
     * Get all the patientProfiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PatientProfileDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PatientProfiles");
        return patientProfileRepository.findAll(pageable).map(patientProfileMapper::toDto);
    }

    /**
     * Get one patientProfile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PatientProfileDTO> findOne(Long id) {
        log.debug("Request to get PatientProfile : {}", id);
        return patientProfileRepository.findById(id).map(patientProfileMapper::toDto);
    }

    /**
     * Delete the patientProfile by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PatientProfile : {}", id);
        patientProfileRepository.deleteById(id);
    }
}
