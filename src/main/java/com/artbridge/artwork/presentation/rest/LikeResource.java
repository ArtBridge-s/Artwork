package com.artbridge.artwork.presentation.rest;

import com.artbridge.artwork.domain.model.Like;
import com.artbridge.artwork.infrastructure.repository.LikeRepository;
import com.artbridge.artwork.infrastructure.security.SecurityUtils;
import com.artbridge.artwork.infrastructure.security.jwt.TokenProvider;
import com.artbridge.artwork.application.usecase.LikeUsecase;
import com.artbridge.artwork.application.dto.LikeDTO;
import com.artbridge.artwork.application.dto.MemberDTO;
import com.artbridge.artwork.presentation.errors.BadRequestAlertException;
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
 * REST controller for managing {@link Like}.
 */
@RestController
@RequestMapping("/api/likes")
public class LikeResource {

    private final Logger log = LoggerFactory.getLogger(LikeResource.class);

    private static final String ENTITY_NAME = "artworkLike";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LikeUsecase likeUsecase;

    private final LikeRepository likeRepository;

    private final TokenProvider tokenProvider;


    public LikeResource(LikeUsecase likeUsecase, LikeRepository likeRepository, TokenProvider tokenProvider) {
        this.likeUsecase = likeUsecase;
        this.likeRepository = likeRepository;
        this.tokenProvider = tokenProvider;
    }



    /**
     * {@code POST /likes} : Like을 생성합니다.
     *
     * @param likeDTO 생성할 Like의 정보를 담은 LikeDTO 객체
     * @return 생성된 Like의 정보를 담은 ResponseEntity
     * @throws URISyntaxException URI 구문 예외가 발생하는 경우
     * @throws BadRequestAlertException Like 생성 실패 시 발생하는 BadRequestAlertException
     */
    @PostMapping
    public ResponseEntity<LikeDTO> createLike(@RequestBody LikeDTO likeDTO) throws URISyntaxException {
        log.debug("REST request to save Like : {}", likeDTO);
        if (likeDTO.getId() != null) {
            throw new BadRequestAlertException("A new like cannot already have an ID", ENTITY_NAME, "idexists");
        }

        String token = this.validateAndGetToken();

        if(likeRepository.existsByArtwork_IdAndMember_Id(likeDTO.getArtwork().getId(), this.tokenProvider.getUserIdFromToken(token))) {
            throw new BadRequestAlertException("이미 좋아요를 누른 작품입니다.", ENTITY_NAME, "alreadyliked");
        }

        MemberDTO memberDTO = this.createMember(token);

        likeDTO.setMember(memberDTO);

        LikeDTO result = likeUsecase.save(likeDTO);
        return ResponseEntity
            .created(new URI("/api/likes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }



    /**
     * {@code PUT  /likes/:id} : Updates an existing like.
     *
     * @param id the id of the likeDTO to save.
     * @param likeDTO the likeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated likeDTO,
     * or with status {@code 400 (Bad Request)} if the likeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the likeDTO couldn't be updated.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LikeDTO> updateLike(@PathVariable(value = "id", required = false) final Long id, @RequestBody LikeDTO likeDTO) {
        log.debug("REST request to update Like : {}, {}", id, likeDTO);
        this.validateLike(id, likeDTO);

        LikeDTO result = likeUsecase.update(likeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, likeDTO.getId().toString()))
            .body(result);
    }



    /**
     * {@code PATCH  /likes/:id} : Partial updates given fields of an existing like, field will ignore if it is null
     *
     * @param id the id of the likeDTO to save.
     * @param likeDTO the likeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated likeDTO,
     * or with status {@code 400 (Bad Request)} if the likeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the likeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the likeDTO couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LikeDTO> partialUpdateLike(@PathVariable(value = "id", required = false) final Long id, @RequestBody LikeDTO likeDTO) {
        log.debug("REST request to partial update Like partially : {}, {}", id, likeDTO);
        this.validateLike(id, likeDTO);

        Optional<LikeDTO> result = likeUsecase.partialUpdate(likeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, likeDTO.getId().toString())
        );
    }


    /**
     * {@code GET  /likes} : get all the likes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of likes in body.
     */
    @GetMapping
    public ResponseEntity<List<LikeDTO>> getAllLikes(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Likes");
        Page<LikeDTO> page = likeUsecase.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }



    /**
     * {@code GET  /likes/:id} : get the "id" like.
     *
     * @param id the id of the likeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the likeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LikeDTO> getLike(@PathVariable Long id) {
        log.debug("REST request to get Like : {}", id);
        Optional<LikeDTO> likeDTO = likeUsecase.findOne(id);
        return ResponseUtil.wrapOrNotFound(likeDTO);
    }



    /**
     * {@code DELETE /likes/{id}} : Like을 삭제합니다.
     *
     * @param artworkId 삭제할 Like의 Artwork ID
     * @return 삭제된 Like 정보를 담은 ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable(value = "id") Long artworkId) {
        log.debug("REST request to delete Like : {}", artworkId);
        String token = this.validateAndGetToken();
        MemberDTO memberDTO = this.createMember(token);

        likeUsecase.delete(artworkId, memberDTO.getId());
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, artworkId.toString()))
            .build();
    }



    /**
     * {@code GET /likes/counts} : Artwork에 대한 Like 개수를 조회합니다.
     *
     * @param artworkId Artwork의 식별자(ID)
     * @return Artwork에 대한 Like 개수를 담은 ResponseEntity
     */
    @GetMapping("/counts")
    public ResponseEntity<Long> getLikeCount(@RequestParam Long artworkId) {
        log.debug("REST request to get Like Count : {}", artworkId);
        Long count = likeUsecase.countByArtworkId(artworkId);
        return ResponseEntity.ok().body(count);
    }



    /**
     * Like 정보를 검증합니다.
     *
     * @param id       Like의 식별자(ID)
     * @param likeDTO  검증할 LikeDTO 객체
     * @throws BadRequestAlertException 검증 실패 시 발생하는 BadRequestAlertException
     */
    private void validateLike(Long id, LikeDTO likeDTO) {
        if (likeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, likeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!likeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
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
