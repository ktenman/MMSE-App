package ee.tenman.mmse.web.rest;

import ee.tenman.mmse.IntegrationTest;
import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.User;
import ee.tenman.mmse.repository.TestEntityRepository;
import ee.tenman.mmse.service.TestEntityService;
import ee.tenman.mmse.service.dto.TestEntityDTO;
import ee.tenman.mmse.service.mapper.TestEntityMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link TestEntityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TestEntityResourceIT {

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;

    private static final String ENTITY_API_URL = "/api/test-entities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Instant NOW = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TestEntityRepository testEntityRepository;

    @Mock
    private TestEntityRepository testEntityRepositoryMock;

    @Autowired
    private TestEntityMapper testEntityMapper;

    @Mock
    private TestEntityService testEntityServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTestEntityMockMvc;

    private TestEntity testEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestEntity createEntity(EntityManager em) {
        TestEntity testEntity = new TestEntity();
        testEntity.setScore(DEFAULT_SCORE);

        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();

        PatientProfile patientProfile = PatientProfileResourceIT.createEntity(em);
        em.persist(patientProfile);
        em.flush();

        testEntity.setUser(user);
        testEntity.setPatientProfile(patientProfile);
        return testEntity;
    }

    @BeforeEach
    public void initTest() {
        testEntity = createEntity(em);
    }

    @Test
    @Transactional
    void createTestEntity() throws Exception {
        List<TestEntity> testEntities = testEntityRepository.findAll();
        assertThat(testEntities).isEmpty();
        // Create the TestEntity
        TestEntityDTO testEntityDTO = testEntityMapper.toDto(testEntity);
        restTestEntityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(testEntityDTO)))
            .andExpect(status().isCreated());

        // Validate the TestEntity in the database
        testEntities = testEntityRepository.findAll();
        assertThat(testEntities).hasSize(1);
        TestEntity testTestEntity = testEntities.get(0);
        assertThat(testTestEntity.getCreatedAt()).isNotNull().isCloseTo(NOW, within(1, MINUTES));
        assertThat(testTestEntity.getUpdatedAt()).isNotNull().isCloseTo(NOW, within(1, MINUTES));
        assertThat(testTestEntity.getScore()).isEqualTo(DEFAULT_SCORE);
    }

    @Test
    @Transactional
    void createTestEntityWithExistingId() throws Exception {
        // Create the TestEntity with an existing ID
        testEntity.setId(1L);
        TestEntityDTO testEntityDTO = testEntityMapper.toDto(testEntity);

        int databaseSizeBeforeCreate = testEntityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTestEntityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(testEntityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTestEntities() throws Exception {
        // Initialize the database
        testEntityRepository.saveAndFlush(testEntity);

        // Get all the testEntityList
        restTestEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(notNullValue()))
            .andExpect(jsonPath("$.[*].updatedAt").value(notNullValue()))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)));
    }

    @SuppressWarnings({"unchecked"})
    void getAllTestEntitiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(testEntityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTestEntityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(testEntityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    void getAllTestEntitiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(testEntityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTestEntityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(testEntityRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTestEntity() throws Exception {
        // Initialize the database
        testEntityRepository.saveAndFlush(testEntity);

        // Get the testEntity
        restTestEntityMockMvc
            .perform(get(ENTITY_API_URL_ID, testEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(testEntity.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(notNullValue()))
            .andExpect(jsonPath("$.updatedAt").value(notNullValue()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE));
    }

    @Test
    @Transactional
    void getNonExistingTestEntity() throws Exception {
        // Get the testEntity
        restTestEntityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTestEntity() throws Exception {
        // Initialize the database
        testEntityRepository.saveAndFlush(testEntity);

        int databaseSizeBeforeUpdate = testEntityRepository.findAll().size();

        // Update the testEntity
        TestEntity updatedTestEntity = testEntityRepository.findById(testEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTestEntity are not directly saved in db
        em.detach(updatedTestEntity);
        updatedTestEntity.setScore(UPDATED_SCORE);
        TestEntityDTO testEntityDTO = testEntityMapper.toDto(updatedTestEntity);

        restTestEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, testEntityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(testEntityDTO))
            )
            .andExpect(status().isOk());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeUpdate);
        TestEntity testTestEntity = testEntityList.get(testEntityList.size() - 1);
        assertThat(testTestEntity.getCreatedAt()).isNotNull().isCloseTo(NOW, within(1, MINUTES));
        assertThat(testTestEntity.getUpdatedAt()).isNotNull().isCloseTo(NOW, within(1, MINUTES));
        assertThat(testTestEntity.getScore()).isEqualTo(UPDATED_SCORE);
    }

    @Test
    @Transactional
    void putNonExistingTestEntity() throws Exception {
        int databaseSizeBeforeUpdate = testEntityRepository.findAll().size();
        testEntity.setId(longCount.incrementAndGet());

        // Create the TestEntity
        TestEntityDTO testEntityDTO = testEntityMapper.toDto(testEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, testEntityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(testEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTestEntity() throws Exception {
        int databaseSizeBeforeUpdate = testEntityRepository.findAll().size();
        testEntity.setId(longCount.incrementAndGet());

        // Create the TestEntity
        TestEntityDTO testEntityDTO = testEntityMapper.toDto(testEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(testEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTestEntity() throws Exception {
        int databaseSizeBeforeUpdate = testEntityRepository.findAll().size();
        testEntity.setId(longCount.incrementAndGet());

        // Create the TestEntity
        TestEntityDTO testEntityDTO = testEntityMapper.toDto(testEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestEntityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(testEntityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTestEntityWithPatch() throws Exception {
        // Initialize the database
        testEntityRepository.saveAndFlush(testEntity);

        int databaseSizeBeforeUpdate = testEntityRepository.findAll().size();

        // Update the testEntity using partial update
        TestEntity partialUpdatedTestEntity = new TestEntity();
        partialUpdatedTestEntity.setId(testEntity.getId());

        partialUpdatedTestEntity.setScore(UPDATED_SCORE);

        restTestEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTestEntity))
            )
            .andExpect(status().isOk());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeUpdate);
        TestEntity testTestEntity = testEntityList.get(testEntityList.size() - 1);
        assertThat(testTestEntity.getCreatedAt()).isNotNull().isCloseTo(NOW, within(1, MINUTES));
        assertThat(testTestEntity.getUpdatedAt()).isNotNull().isCloseTo(NOW, within(1, MINUTES));
        assertThat(testTestEntity.getScore()).isEqualTo(UPDATED_SCORE);
    }

    @Test
    @Transactional
    void fullUpdateTestEntityWithPatch() throws Exception {
        // Initialize the database
        testEntityRepository.saveAndFlush(testEntity);

        int databaseSizeBeforeUpdate = testEntityRepository.findAll().size();

        // Update the testEntity using partial update
        TestEntity partialUpdatedTestEntity = new TestEntity();
        partialUpdatedTestEntity.setId(testEntity.getId());

        partialUpdatedTestEntity.setScore(UPDATED_SCORE);

        restTestEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTestEntity))
            )
            .andExpect(status().isOk());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeUpdate);
        TestEntity testTestEntity = testEntityList.get(testEntityList.size() - 1);
        assertThat(testTestEntity.getCreatedAt()).isNotNull().isCloseTo(NOW, within(1, MINUTES));
        assertThat(testTestEntity.getUpdatedAt()).isNotNull().isCloseTo(NOW, within(1, MINUTES));
        assertThat(testTestEntity.getScore()).isEqualTo(UPDATED_SCORE);
    }

    @Test
    @Transactional
    void patchNonExistingTestEntity() throws Exception {
        int databaseSizeBeforeUpdate = testEntityRepository.findAll().size();
        testEntity.setId(longCount.incrementAndGet());

        // Create the TestEntity
        TestEntityDTO testEntityDTO = testEntityMapper.toDto(testEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, testEntityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(testEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTestEntity() throws Exception {
        int databaseSizeBeforeUpdate = testEntityRepository.findAll().size();
        testEntity.setId(longCount.incrementAndGet());

        // Create the TestEntity
        TestEntityDTO testEntityDTO = testEntityMapper.toDto(testEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(testEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTestEntity() throws Exception {
        int databaseSizeBeforeUpdate = testEntityRepository.findAll().size();
        testEntity.setId(longCount.incrementAndGet());

        // Create the TestEntity
        TestEntityDTO testEntityDTO = testEntityMapper.toDto(testEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestEntityMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(testEntityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestEntity in the database
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTestEntity() throws Exception {
        // Initialize the database
        testEntityRepository.saveAndFlush(testEntity);

        int databaseSizeBeforeDelete = testEntityRepository.findAll().size();

        // Delete the testEntity
        restTestEntityMockMvc
            .perform(delete(ENTITY_API_URL_ID, testEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TestEntity> testEntityList = testEntityRepository.findAll();
        assertThat(testEntityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
