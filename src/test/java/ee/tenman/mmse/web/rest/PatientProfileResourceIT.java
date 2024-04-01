package ee.tenman.mmse.web.rest;

import ee.tenman.mmse.IntegrationTest;
import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.repository.PatientProfileRepository;
import ee.tenman.mmse.service.dto.PatientProfileDTO;
import ee.tenman.mmse.service.mapper.PatientProfileMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link PatientProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PatientProfileResourceIT {

    private static final String DEFAULT_PATIENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_PATIENT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/patient-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final Instant NOW = Instant.now();
    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private PatientProfileMapper patientProfileMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPatientProfileMockMvc;

    private PatientProfile patientProfile;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PatientProfile createEntity(EntityManager em) {
        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setPatientId(DEFAULT_PATIENT_ID);
        patientProfile.setName(DEFAULT_NAME);
        patientProfile.setCreatedAt(DEFAULT_CREATED_AT);
        patientProfile.setUpdatedAt(DEFAULT_UPDATED_AT);
        return patientProfile;
    }

    @BeforeEach
    public void initTest() {
        patientProfile = createEntity(em);
    }

    @Test
    @Transactional
    void createPatientProfile() throws Exception {
        int databaseSizeBeforeCreate = patientProfileRepository.findAll().size();
        // Create the PatientProfile
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);
        restPatientProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isCreated());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeCreate + 1);
        PatientProfile testPatientProfile = patientProfileList.get(patientProfileList.size() - 1);
        assertThat(testPatientProfile.getPatientId()).isEqualTo(DEFAULT_PATIENT_ID);
        assertThat(testPatientProfile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPatientProfile.getCreatedAt()).isNotNull().isAfter(NOW);
        assertThat(testPatientProfile.getUpdatedAt()).isNotNull().isAfter(NOW);
    }

    @Test
    @Transactional
    void createPatientProfileWithExistingId() throws Exception {
        // Create the PatientProfile with an existing ID
        patientProfile.setId(1L);
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        int databaseSizeBeforeCreate = patientProfileRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPatientProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPatientIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = patientProfileRepository.findAll().size();
        // set the field null
        patientProfile.setPatientId(null);

        // Create the PatientProfile, which fails.
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        restPatientProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isBadRequest());

        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = patientProfileRepository.findAll().size();
        // set the field null
        patientProfile.setName(null);

        // Create the PatientProfile, which fails.
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        restPatientProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isBadRequest());

        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = patientProfileRepository.findAll().size();
        // set the field null
        patientProfile.setCreatedAt(null);

        // Create the PatientProfile, which fails.
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        restPatientProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isBadRequest());

        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = patientProfileRepository.findAll().size();
        // set the field null
        patientProfile.setUpdatedAt(null);

        // Create the PatientProfile, which fails.
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        restPatientProfileMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isBadRequest());

        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPatientProfiles() throws Exception {
        // Initialize the database
        patientProfileRepository.saveAndFlush(patientProfile);

        // Get all the patientProfileList
        restPatientProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(patientProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].patientId").value(hasItem(DEFAULT_PATIENT_ID)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(notNullValue()))
            .andExpect(jsonPath("$.[*].updatedAt").value(notNullValue()));
    }

    @Test
    @Transactional
    void getPatientProfile() throws Exception {
        // Initialize the database
        patientProfileRepository.saveAndFlush(patientProfile);

        // Get the patientProfile
        restPatientProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, patientProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(patientProfile.getId().intValue()))
            .andExpect(jsonPath("$.patientId").value(DEFAULT_PATIENT_ID))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(notNullValue()))
            .andExpect(jsonPath("$.updatedAt").value(notNullValue()));
    }

    @Test
    @Transactional
    void getNonExistingPatientProfile() throws Exception {
        // Get the patientProfile
        restPatientProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPatientProfile() throws Exception {
        // Initialize the database
        patientProfileRepository.saveAndFlush(patientProfile);

        int databaseSizeBeforeUpdate = patientProfileRepository.findAll().size();

        // Update the patientProfile
        PatientProfile updatedPatientProfile = patientProfileRepository.findById(patientProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPatientProfile are not directly saved in db
        em.detach(updatedPatientProfile);
        updatedPatientProfile.setPatientId(UPDATED_PATIENT_ID);
        updatedPatientProfile.setName(UPDATED_NAME);
        updatedPatientProfile.setCreatedAt(UPDATED_CREATED_AT);
        updatedPatientProfile.setUpdatedAt(UPDATED_UPDATED_AT);
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(updatedPatientProfile);

        restPatientProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, patientProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeUpdate);
        PatientProfile testPatientProfile = patientProfileList.get(patientProfileList.size() - 1);
        assertThat(testPatientProfile.getPatientId()).isEqualTo(UPDATED_PATIENT_ID);
        assertThat(testPatientProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPatientProfile.getCreatedAt()).isNotNull().isCloseTo(NOW, within(1, ChronoUnit.MINUTES));
        assertThat(testPatientProfile.getUpdatedAt()).isNotNull().isCloseTo(NOW, within(1, ChronoUnit.MINUTES));
    }

    @Test
    @Transactional
    void putNonExistingPatientProfile() throws Exception {
        int databaseSizeBeforeUpdate = patientProfileRepository.findAll().size();
        patientProfile.setId(longCount.incrementAndGet());

        // Create the PatientProfile
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatientProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, patientProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPatientProfile() throws Exception {
        int databaseSizeBeforeUpdate = patientProfileRepository.findAll().size();
        patientProfile.setId(longCount.incrementAndGet());

        // Create the PatientProfile
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPatientProfile() throws Exception {
        int databaseSizeBeforeUpdate = patientProfileRepository.findAll().size();
        patientProfile.setId(longCount.incrementAndGet());

        // Create the PatientProfile
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientProfileMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePatientProfileWithPatch() throws Exception {
        // Initialize the database
        patientProfileRepository.saveAndFlush(patientProfile);

        int databaseSizeBeforeUpdate = patientProfileRepository.findAll().size();

        // Update the patientProfile using partial update
        PatientProfile partialUpdatedPatientProfile = new PatientProfile();
        partialUpdatedPatientProfile.setId(patientProfile.getId());
        partialUpdatedPatientProfile.setName(UPDATED_NAME);

        restPatientProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPatientProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPatientProfile))
            )
            .andExpect(status().isOk());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeUpdate);
        PatientProfile testPatientProfile = patientProfileList.get(patientProfileList.size() - 1);
        assertThat(testPatientProfile.getPatientId()).isEqualTo(DEFAULT_PATIENT_ID);
        assertThat(testPatientProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPatientProfile.getCreatedAt()).isNotNull().isCloseTo(NOW, within(1, ChronoUnit.MINUTES));
        assertThat(testPatientProfile.getUpdatedAt()).isNotNull().isCloseTo(NOW, within(1, ChronoUnit.MINUTES));
    }

    @Test
    @Transactional
    void fullUpdatePatientProfileWithPatch() throws Exception {
        // Initialize the database
        patientProfileRepository.saveAndFlush(patientProfile);

        int databaseSizeBeforeUpdate = patientProfileRepository.findAll().size();

        // Update the patientProfile using partial update
        PatientProfile partialUpdatedPatientProfile = new PatientProfile();
        partialUpdatedPatientProfile.setId(patientProfile.getId());

        partialUpdatedPatientProfile.setPatientId(UPDATED_PATIENT_ID);
        partialUpdatedPatientProfile.setName(UPDATED_NAME);
        partialUpdatedPatientProfile.setCreatedAt(UPDATED_CREATED_AT);
        partialUpdatedPatientProfile.setUpdatedAt(UPDATED_UPDATED_AT);

        restPatientProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPatientProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPatientProfile))
            )
            .andExpect(status().isOk());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeUpdate);
        PatientProfile testPatientProfile = patientProfileList.get(patientProfileList.size() - 1);
        assertThat(testPatientProfile.getPatientId()).isEqualTo(UPDATED_PATIENT_ID);
        assertThat(testPatientProfile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPatientProfile.getCreatedAt()).isCloseTo(NOW, within(1, ChronoUnit.MINUTES));
        assertThat(testPatientProfile.getUpdatedAt()).isCloseTo(NOW, within(1, ChronoUnit.MINUTES));
    }

    @Test
    @Transactional
    void patchNonExistingPatientProfile() throws Exception {
        int databaseSizeBeforeUpdate = patientProfileRepository.findAll().size();
        patientProfile.setId(longCount.incrementAndGet());

        // Create the PatientProfile
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatientProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, patientProfileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPatientProfile() throws Exception {
        int databaseSizeBeforeUpdate = patientProfileRepository.findAll().size();
        patientProfile.setId(longCount.incrementAndGet());

        // Create the PatientProfile
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPatientProfile() throws Exception {
        int databaseSizeBeforeUpdate = patientProfileRepository.findAll().size();
        patientProfile.setId(longCount.incrementAndGet());

        // Create the PatientProfile
        PatientProfileDTO patientProfileDTO = patientProfileMapper.toDto(patientProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientProfileMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(patientProfileDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PatientProfile in the database
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePatientProfile() throws Exception {
        // Initialize the database
        patientProfileRepository.saveAndFlush(patientProfile);

        int databaseSizeBeforeDelete = patientProfileRepository.findAll().size();

        // Delete the patientProfile
        restPatientProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, patientProfile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PatientProfile> patientProfileList = patientProfileRepository.findAll();
        assertThat(patientProfileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
