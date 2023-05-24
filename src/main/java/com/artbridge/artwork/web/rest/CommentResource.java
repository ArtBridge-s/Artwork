package com.artbridge.artwork.web.rest;

import com.artbridge.artwork.repository.CommentRepository;
import com.artbridge.artwork.security.SecurityUtils;
import com.artbridge.artwork.security.jwt.TokenProvider;
import com.artbridge.artwork.service.CommentService;
import com.artbridge.artwork.service.dto.CommentDTO;
import com.artbridge.artwork.service.dto.MemberDTO;
import com.artbridge.artwork.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.artbridge.artwork.domain.Comment}.
 */
@RestController
@RequestMapping("/api")
public class CommentResource {

    private final Logger log = LoggerFactory.getLogger(CommentResource.class);

    private static final String ENTITY_NAME = "artworkComment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommentService commentService;

    private final CommentRepository commentRepository;

    private final TokenProvider tokenProvider;

    public CommentResource(CommentService commentService, CommentRepository commentRepository, TokenProvider tokenProvider) {
        this.commentService = commentService;
        this.commentRepository = commentRepository;
        this.tokenProvider = tokenProvider;
    }

    /**
     * {@code POST  /comments} : Create a new comment.
     *
     * @param commentDTO the commentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commentDTO, or with status {@code 400 (Bad Request)} if the comment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/comments")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) throws URISyntaxException {
        log.debug("REST request to save Comment : {}", commentDTO);
        if (commentDTO.getId() != null) {
            throw new BadRequestAlertException("A new comment cannot already have an ID", ENTITY_NAME, "idexists");
        }

        String token = this.validateAndGetToken();
        MemberDTO memberDTO = this.createMember(token);
        commentDTO.setMember(memberDTO);

        CommentDTO result = commentService.save(commentDTO);
        return ResponseEntity
            .created(new URI("/api/comments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /comments/:id} : Updates an existing comment.
     *
     * @param id the id of the commentDTO to save.
     * @param commentDTO the commentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commentDTO,
     * or with status {@code 400 (Bad Request)} if the commentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable(value = "id", required = false) final Long id, @RequestBody CommentDTO commentDTO) throws URISyntaxException {
        log.debug("REST request to update Comment : {}, {}", id, commentDTO);
        if (commentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CommentDTO result = commentService.update(commentDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commentDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /comments/:id} : Partial updates given fields of an existing comment, field will ignore if it is null
     *
     * @param id the id of the commentDTO to save.
     * @param commentDTO the commentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commentDTO,
     * or with status {@code 400 (Bad Request)} if the commentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the commentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the commentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/comments/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CommentDTO> partialUpdateComment(@PathVariable(value = "id", required = false) final Long id, @RequestBody CommentDTO commentDTO) throws URISyntaxException {
        log.debug("REST request to partial update Comment partially : {}, {}", id, commentDTO);
        if (commentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CommentDTO> result = commentService.partialUpdate(commentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /comments} : get all the comments.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of comments in body.
     */
    @GetMapping("/comments")
    public ResponseEntity<List<CommentDTO>> getAllComments(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Comments");
        Page<CommentDTO> page = commentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /comments/:id} : get the "id" comment.
     *
     * @param id the id of the commentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long id) {
        log.debug("REST request to get Comment : {}", id);
        Optional<CommentDTO> commentDTO = commentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(commentDTO);
    }

    /**
     * {@code DELETE  /comments/:id} : delete the "id" comment.
     *
     * @param id the id of the commentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.debug("REST request to delete Comment : {}", id);
        commentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }





    /**
     * 현재 사용자로부터 얻은 JWT 토큰을 유효성 검사하고 유효한 토큰을 반환합니다.
     *
     * @return 유효한 JWT 토큰
     * @throws BadRequestAlertException JWT 토큰이 잘못되었거나 존재하지 않는 경우
     */
    private String validateAndGetToken() { /*TODO -REFACTOR*/
        Optional<String> optToken = SecurityUtils.getCurrentUserJWT();
        if (optToken.isEmpty() || !this.tokenProvider.validateToken(optToken.get())) {
            throw new BadRequestAlertException("Invalid JWT token", ENTITY_NAME, "invalidtoken");
        }
        return optToken.get();
    }

    /**
     * 주어진 토큰을 사용하여 MemberDTO 객체를 생성합니다.
     *
     * @param token JWT 토큰
     * @return MemberDTO 객체
     */
    private MemberDTO createMember(String token) { /*TODO -REFACTOR*/
        Authentication authentication = this.tokenProvider.getAuthentication(token);
        Long userId = this.tokenProvider.getUserIdFromToken(token);
        return new MemberDTO(userId,  authentication.getName());
    }
}
