package ee.tenman.mmse.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ee.tenman.mmse.IntegrationTest;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.dto.UserAnswerDTO;
import ee.tenman.mmse.service.mapper.UserAnswerMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserAnswerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserAnswerResourceIT {

    private static final String DEFAULT_ANSWER_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER_TEXT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final QuestionId DEFAULT_QUESTION_ID = QuestionId.QUESTION_1;
    private static final QuestionId UPDATED_QUESTION_ID = QuestionId.QUESTION_1;

    private static final String ENTITY_API_URL = "/api/user-answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserAnswerMockMvc;

    private UserAnswer userAnswer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAnswer createEntity(EntityManager em) {
        UserAnswer userAnswer = new UserAnswer()
            .answerText(DEFAULT_ANSWER_TEXT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .questionId(DEFAULT_QUESTION_ID);
        // Add required entity
        TestEntity testEntity;
        if (TestUtil.findAll(em, TestEntity.class).isEmpty()) {
            testEntity = TestEntityResourceIT.createEntity(em);
            em.persist(testEntity);
            em.flush();
        } else {
            testEntity = TestUtil.findAll(em, TestEntity.class).get(0);
        }
        userAnswer.setTestEntity(testEntity);
        return userAnswer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAnswer createUpdatedEntity(EntityManager em) {
        UserAnswer userAnswer = new UserAnswer()
            .answerText(UPDATED_ANSWER_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .questionId(UPDATED_QUESTION_ID);
        // Add required entity
        TestEntity testEntity;
        if (TestUtil.findAll(em, TestEntity.class).isEmpty()) {
            testEntity = TestEntityResourceIT.createUpdatedEntity(em);
            em.persist(testEntity);
            em.flush();
        } else {
            testEntity = TestUtil.findAll(em, TestEntity.class).get(0);
        }
        userAnswer.setTestEntity(testEntity);
        return userAnswer;
    }

    @BeforeEach
    public void initTest() {
        userAnswer = createEntity(em);
    }

    @Test
    @Transactional
    void createUserAnswer() throws Exception {
        int databaseSizeBeforeCreate = userAnswerRepository.findAll().size();
        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);
        restUserAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userAnswerDTO)))
            .andExpect(status().isCreated());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeCreate + 1);
        UserAnswer testUserAnswer = userAnswerList.get(userAnswerList.size() - 1);
        assertThat(testUserAnswer.getAnswerText()).isEqualTo(DEFAULT_ANSWER_TEXT);
        assertThat(testUserAnswer.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testUserAnswer.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testUserAnswer.getQuestionId()).isEqualTo(DEFAULT_QUESTION_ID);
    }

    @Test
    @Transactional
    void createUserAnswerWithExistingId() throws Exception {
        // Create the UserAnswer with an existing ID
        userAnswer.setId(1L);
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        int databaseSizeBeforeCreate = userAnswerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userAnswerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = userAnswerRepository.findAll().size();
        // set the field null
        userAnswer.setCreatedAt(null);

        // Create the UserAnswer, which fails.
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        restUserAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userAnswerDTO)))
            .andExpect(status().isBadRequest());

        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuestionIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = userAnswerRepository.findAll().size();
        // set the field null
        userAnswer.setQuestionId(null);

        // Create the UserAnswer, which fails.
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        restUserAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userAnswerDTO)))
            .andExpect(status().isBadRequest());

        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserAnswers() throws Exception {
        // Initialize the database
        userAnswerRepository.saveAndFlush(userAnswer);

        // Get all the userAnswerList
        restUserAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAnswer.getId().intValue())))
            .andExpect(jsonPath("$.[*].answerText").value(hasItem(DEFAULT_ANSWER_TEXT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].questionId").value(hasItem(DEFAULT_QUESTION_ID.toString())));
    }

    @Test
    @Transactional
    void getUserAnswer() throws Exception {
        // Initialize the database
        userAnswerRepository.saveAndFlush(userAnswer);

        // Get the userAnswer
        restUserAnswerMockMvc
            .perform(get(ENTITY_API_URL_ID, userAnswer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userAnswer.getId().intValue()))
            .andExpect(jsonPath("$.answerText").value(DEFAULT_ANSWER_TEXT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.questionId").value(DEFAULT_QUESTION_ID.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserAnswer() throws Exception {
        // Get the userAnswer
        restUserAnswerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserAnswer() throws Exception {
        // Initialize the database
        userAnswerRepository.saveAndFlush(userAnswer);

        int databaseSizeBeforeUpdate = userAnswerRepository.findAll().size();

        // Update the userAnswer
        UserAnswer updatedUserAnswer = userAnswerRepository.findById(userAnswer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserAnswer are not directly saved in db
        em.detach(updatedUserAnswer);
        updatedUserAnswer
            .answerText(UPDATED_ANSWER_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .questionId(UPDATED_QUESTION_ID);
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(updatedUserAnswer);

        restUserAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userAnswerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userAnswerDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeUpdate);
        UserAnswer testUserAnswer = userAnswerList.get(userAnswerList.size() - 1);
        assertThat(testUserAnswer.getAnswerText()).isEqualTo(UPDATED_ANSWER_TEXT);
        assertThat(testUserAnswer.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserAnswer.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testUserAnswer.getQuestionId()).isEqualTo(UPDATED_QUESTION_ID);
    }

    @Test
    @Transactional
    void putNonExistingUserAnswer() throws Exception {
        int databaseSizeBeforeUpdate = userAnswerRepository.findAll().size();
        userAnswer.setId(count.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userAnswerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserAnswer() throws Exception {
        int databaseSizeBeforeUpdate = userAnswerRepository.findAll().size();
        userAnswer.setId(count.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserAnswer() throws Exception {
        int databaseSizeBeforeUpdate = userAnswerRepository.findAll().size();
        userAnswer.setId(count.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userAnswerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserAnswerWithPatch() throws Exception {
        // Initialize the database
        userAnswerRepository.saveAndFlush(userAnswer);

        int databaseSizeBeforeUpdate = userAnswerRepository.findAll().size();

        // Update the userAnswer using partial update
        UserAnswer partialUpdatedUserAnswer = new UserAnswer();
        partialUpdatedUserAnswer.setId(userAnswer.getId());

        partialUpdatedUserAnswer.createdAt(UPDATED_CREATED_AT).questionId(UPDATED_QUESTION_ID);

        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserAnswer))
            )
            .andExpect(status().isOk());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeUpdate);
        UserAnswer testUserAnswer = userAnswerList.get(userAnswerList.size() - 1);
        assertThat(testUserAnswer.getAnswerText()).isEqualTo(DEFAULT_ANSWER_TEXT);
        assertThat(testUserAnswer.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserAnswer.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testUserAnswer.getQuestionId()).isEqualTo(UPDATED_QUESTION_ID);
    }

    @Test
    @Transactional
    void fullUpdateUserAnswerWithPatch() throws Exception {
        // Initialize the database
        userAnswerRepository.saveAndFlush(userAnswer);

        int databaseSizeBeforeUpdate = userAnswerRepository.findAll().size();

        // Update the userAnswer using partial update
        UserAnswer partialUpdatedUserAnswer = new UserAnswer();
        partialUpdatedUserAnswer.setId(userAnswer.getId());

        partialUpdatedUserAnswer
            .answerText(UPDATED_ANSWER_TEXT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .questionId(UPDATED_QUESTION_ID);

        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserAnswer))
            )
            .andExpect(status().isOk());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeUpdate);
        UserAnswer testUserAnswer = userAnswerList.get(userAnswerList.size() - 1);
        assertThat(testUserAnswer.getAnswerText()).isEqualTo(UPDATED_ANSWER_TEXT);
        assertThat(testUserAnswer.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUserAnswer.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testUserAnswer.getQuestionId()).isEqualTo(UPDATED_QUESTION_ID);
    }

    @Test
    @Transactional
    void patchNonExistingUserAnswer() throws Exception {
        int databaseSizeBeforeUpdate = userAnswerRepository.findAll().size();
        userAnswer.setId(count.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userAnswerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserAnswer() throws Exception {
        int databaseSizeBeforeUpdate = userAnswerRepository.findAll().size();
        userAnswer.setId(count.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userAnswerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserAnswer() throws Exception {
        int databaseSizeBeforeUpdate = userAnswerRepository.findAll().size();
        userAnswer.setId(count.incrementAndGet());

        // Create the UserAnswer
        UserAnswerDTO userAnswerDTO = userAnswerMapper.toDto(userAnswer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userAnswerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAnswer in the database
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserAnswer() throws Exception {
        // Initialize the database
        userAnswerRepository.saveAndFlush(userAnswer);

        int databaseSizeBeforeDelete = userAnswerRepository.findAll().size();

        // Delete the userAnswer
        restUserAnswerMockMvc
            .perform(delete(ENTITY_API_URL_ID, userAnswer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserAnswer> userAnswerList = userAnswerRepository.findAll();
        assertThat(userAnswerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
