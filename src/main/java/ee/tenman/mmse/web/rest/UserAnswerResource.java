package ee.tenman.mmse.web.rest;

import ee.tenman.mmse.repository.UserAnswerRepository;
import ee.tenman.mmse.service.UserAnswerService;
import ee.tenman.mmse.service.dto.UserAnswerDTO;
import ee.tenman.mmse.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link ee.tenman.mmse.domain.UserAnswer}.
 */
@RestController
@RequestMapping("/api")
public class UserAnswerResource {

    private final Logger log = LoggerFactory.getLogger(UserAnswerResource.class);

    private static final String ENTITY_NAME = "userAnswer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserAnswerService userAnswerService;

    private final UserAnswerRepository userAnswerRepository;

    public UserAnswerResource(UserAnswerService userAnswerService, UserAnswerRepository userAnswerRepository) {
        this.userAnswerService = userAnswerService;
        this.userAnswerRepository = userAnswerRepository;
    }

    /**
     * {@code POST  /user-answers} : Create a new userAnswer.
     *
     * @param userAnswerDTO the userAnswerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userAnswerDTO, or with status {@code 400 (Bad Request)} if the userAnswer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-answers")
    public ResponseEntity<UserAnswerDTO> createUserAnswer(@Valid @RequestBody UserAnswerDTO userAnswerDTO) throws URISyntaxException {
        log.debug("REST request to save UserAnswer : {}", userAnswerDTO);
        if (userAnswerDTO.getId() != null) {
            throw new BadRequestAlertException("A new userAnswer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserAnswerDTO result = userAnswerService.save(userAnswerDTO);
        return ResponseEntity
            .created(new URI("/api/user-answers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-answers/:id} : Updates an existing userAnswer.
     *
     * @param id            the id of the userAnswerDTO to save.
     * @param userAnswerDTO the userAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the userAnswerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-answers/{id}")
    public ResponseEntity<UserAnswerDTO> updateUserAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserAnswerDTO userAnswerDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserAnswer : {}, {}", id, userAnswerDTO);
        if (userAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserAnswerDTO result = userAnswerService.update(userAnswerDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userAnswerDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-answers/:id} : Partial updates given fields of an existing userAnswer, field will ignore if it is null
     *
     * @param id            the id of the userAnswerDTO to save.
     * @param userAnswerDTO the userAnswerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAnswerDTO,
     * or with status {@code 400 (Bad Request)} if the userAnswerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userAnswerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userAnswerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-answers/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<UserAnswerDTO> partialUpdateUserAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserAnswerDTO userAnswerDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserAnswer partially : {}, {}", id, userAnswerDTO);
        if (userAnswerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAnswerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserAnswerDTO> result = userAnswerService.partialUpdate(userAnswerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userAnswerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-answers} : get all the userAnswers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userAnswers in body.
     */
    @GetMapping("/user-answers")
    public ResponseEntity<List<UserAnswerDTO>> getAllUserAnswers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of UserAnswers");
        Page<UserAnswerDTO> page = userAnswerService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-answers/:id} : get the "id" userAnswer.
     *
     * @param id the id of the userAnswerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userAnswerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-answers/{id}")
    public ResponseEntity<UserAnswerDTO> getUserAnswer(@PathVariable Long id) {
        log.debug("REST request to get UserAnswer : {}", id);
        Optional<UserAnswerDTO> userAnswerDTO = userAnswerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userAnswerDTO);
    }

    /**
     * {@code DELETE  /user-answers/:id} : delete the "id" userAnswer.
     *
     * @param id the id of the userAnswerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-answers/{id}")
    public ResponseEntity<Void> deleteUserAnswer(@PathVariable Long id) {
        log.debug("REST request to delete UserAnswer : {}", id);
        userAnswerService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
