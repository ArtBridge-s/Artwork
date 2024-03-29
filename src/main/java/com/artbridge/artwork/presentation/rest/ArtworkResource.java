package com.artbridge.artwork.presentation.rest;

import com.artbridge.artwork.infrastructure.gcs.GCSService;
import com.artbridge.artwork.domain.model.Artwork;
import com.artbridge.artwork.infrastructure.repository.ArtworkRepository;
import com.artbridge.artwork.infrastructure.security.AuthoritiesConstants;
import com.artbridge.artwork.infrastructure.security.SecurityUtils;
import com.artbridge.artwork.infrastructure.security.jwt.TokenProvider;
import com.artbridge.artwork.application.usecase.ArtworkUsecase;
import com.artbridge.artwork.application.dto.ArtworkDTO;
import com.artbridge.artwork.application.dto.MemberDTO;
import com.artbridge.artwork.presentation.exception.BadRequestAlertException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Artwork}.
 */
@RestController
@RequestMapping("/api/artworks")
public class ArtworkResource {

    private final Logger log = LoggerFactory.getLogger(ArtworkResource.class);

    private static final String ENTITY_NAME = "artworkArtwork";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ArtworkUsecase artworkUsecase;

    private final ArtworkRepository artworkRepository;

    private final TokenProvider tokenProvider;

    private final GCSService gcsService;

    public ArtworkResource(ArtworkUsecase artworkUsecase, ArtworkRepository artworkRepository, TokenProvider tokenProvider, GCSService gcsService) {
        this.artworkUsecase = artworkUsecase;
        this.artworkRepository = artworkRepository;
        this.tokenProvider = tokenProvider;
        this.gcsService = gcsService;
    }


    /**
     * {@code POST /artworks} : Artwork를 생성 요청합니다.
     * MultipartFile은 업로드된 이미지 파일을 의미하며, ArtworkDTO는 Artwork의 정보를 포함하는 문자열입니다.
     *
     * @param file           업로드된 이미지 파일 (MultipartFile)
     * @param artworkDTOStr  Artwork의 정보를 포함하는 문자열 (JSON 형식의 String)
     * @return 생성된 Artwork의 정보를 담은 ResponseEntity
     * @throws URISyntaxException        URI 구문이 잘못되었을 경우 발생하는 예외
     * @throws JsonProcessingException   ArtworkDTO 문자열을 파싱하는 도중 발생하는 예외
     */
    @PostMapping
    public ResponseEntity<ArtworkDTO> createArtworkRequest(@RequestParam("image") MultipartFile file, @RequestParam("artworkDTO") String artworkDTOStr) throws URISyntaxException, IOException {
        ArtworkDTO artworkDTO = convertToDTO(artworkDTOStr);

        log.debug("REST request to save Artwork : {}", artworkDTO);

        String token = this.validateAndGetToken();

        MemberDTO memberDTO = this.createMember(token);
        artworkDTO.setMember(memberDTO);

        this.uploadImage(file, artworkDTO);

        ArtworkDTO result = this.artworkUsecase.saveRequest(artworkDTO);
        return ResponseEntity
            .created(new URI("/api/artworks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }



    /**
     * {@code GET /artworks} : 모든 Artwork를 페이지별로 조회합니다.
     *
     * @param pageable 페이지 정보 (Pageable)
     * @return 페이지별로 조회된 Artwork 목록을 담은 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<ArtworkDTO>> getAllArtworks(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Artworks");
        Page<ArtworkDTO> page = artworkUsecase.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }



    /**
     * {@code GET  /artworks/:id} : 주어진 id에 해당하는 Artwork를 조회합니다.
     *
     * @param id 조회할 Artwork의 식별자(ID)
     * @return 주어진 id에 해당하는 Artwork의 정보를 담은 ResponseEntity
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArtworkDTO> getArtwork(@PathVariable Long id) {
        log.debug("REST request to get Artwork : {}", id);
        Optional<ArtworkDTO> artworkDTO = artworkUsecase.findOne(id);
        return ResponseUtil.wrapOrNotFound(artworkDTO);
    }


    /**
     * {@code PUT  /{id}} : 작품을 업데이트합니다.
     *
     * @param id           작품의 ID
     * @param artworkDTO   업데이트할 작품 데이터
     * @return             {@link ResponseEntity} 객체로 응답 상태와 업데이트된 작품 정보를 담은 응답을 반환합니다.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArtworkDTO> updateArtwork(@PathVariable(value = "id", required = false) final Long id, @RequestBody ArtworkDTO artworkDTO) {
        log.debug("REST request to update Artwork : {}, {}", id, artworkDTO);

        this.validateId(id, artworkDTO);
        this.validateArtworkExists(id);

        ArtworkDTO result = artworkUsecase.update(artworkDTO);

        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, artworkDTO.getId().toString())).body(result);
    }



    /**
     * {@code PATCH  /artworks/:id} : Partial updates given fields of an existing artwork, field will ignore if it is null
     *
     * @param id         the id of the artworkDTO to save.
     * @param artworkDTO the artworkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated artworkDTO,
     * or with status {@code 400 (Bad Request)} if the artworkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the artworkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the artworkDTO couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<ArtworkDTO> partialUpdateArtwork(@PathVariable(value = "id", required = false) final Long id, @RequestBody ArtworkDTO artworkDTO) {
        log.debug("REST request to partial update Artwork partially : {}, {}", id, artworkDTO);
        this.validateId(id, artworkDTO);
        Artwork artwork = this.validateArtworkExists(id);
        this.validateOwnership(artwork);

        Optional<ArtworkDTO> result = artworkUsecase.partialUpdate(artworkDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, artworkDTO.getId().toString())
        );
    }



    /**
     * {@code PATCH /artworks/{id}/authorized/ok} : Artwork를 승인 처리합니다.
     *
     * @param id 승인할 Artwork의 식별자(ID)
     * @return 승인된 Artwork의 정보를 담은 ResponseEntity
     * @throws BadRequestAlertException Artwork 승인 처리 실패 시 발생하는 BadRequestAlertException
     */
    @PatchMapping(value = "/{id}/authorized/ok")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ArtworkDTO> authorizeOkArtwork(@PathVariable(value = "id", required = false) final Long id) {
        log.debug("REST request to authorize ok Artwork : {}", id);

        if (!artworkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        this.validateArtworkExists(id);

        ArtworkDTO result = artworkUsecase.authorizeOkArtwork(id);

        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString())).body(result);
    }



    /**
     * {@code DELETE  /artworks/:id} : "id"에 해당하는 Artwork를 삭제합니다.
     * Artwork 삭제는 관리자 권한인 경우 즉시 삭제되며, 일반 사용자인 경우 삭제 대기 상태로 변경됩니다.
     *
     * @param id          삭제할 Artwork의 식별자(ID)
     * @param artworkDTO  업데이트할 Artwork의 정보를 담은 ArtworkDTO 객체
     * @return 업데이트된 Artwork의 정보를 담은 ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ArtworkDTO> deleteArtwork(@PathVariable Long id, @RequestBody ArtworkDTO artworkDTO) {
        log.debug("REST request to delete Artwork : {}", id);
        this.validateId(id, artworkDTO);
        this.validateArtworkExists(id);

        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            artworkUsecase.delete(id);
            return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
        }

        ArtworkDTO result = artworkUsecase.deletePending(artworkDTO);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())).body(result);
    }





    /**
     * {@code GET /artworks/pendingList/Create} : 이 엔드포인트는 Create 보류 중인 Artwork의 페이지된 목록을 검색합니다.
     *
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기, 정렬)가 포함된 객체
     * @return 상태 코드 200 (OK)와 몸체에 포함된 ArtworkDTO 목록을 가진 ResponseEntity
     * @throws IllegalArgumentException pageable 매개변수가 null인 경우 발생합니다.
     */
    @GetMapping("/pending/creates")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<ArtworkDTO>> getCreatePendings(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Artworks");
        Page<ArtworkDTO> page = artworkUsecase.findCreatePendings(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }



    /**
     * {@code GET /artworks/pending/updates} : 관리자 권한이 있는 경우 업데이트 대기 중인 Artwork 목록을 페이지별로 조회합니다.
     *
     * @param pageable 페이지 정보 (Pageable)
     * @return 페이지별로 조회된 업데이트 대기 중인 Artwork 목록을 담은 ResponseEntity
     */
    @GetMapping("/pending/updates")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<ArtworkDTO>> getUpdatePendings(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Artworks");
        Page<ArtworkDTO> page = artworkUsecase.findUpdatePendings(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


    /**
     * {@code GET /artworks/pending/deletes} : 관리자 권한이 있는 경우 삭제 대기 중인 Artwork 목록을 페이지별로 조회합니다.
     *
     * @param pageable 페이지 정보 (Pageable)
     * @return 페이지별로 조회된 삭제 대기 중인 Artwork 목록을 담은 ResponseEntity
     */
    @GetMapping("/pending/deletes")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<ArtworkDTO>> getDeletePendings(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Artworks");
        Page<ArtworkDTO> page = artworkUsecase.findDeletePendings(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


    /**
     * 관리자 권한으로 Artwork를 생성합니다.
     *
     * @param file           업로드할 이미지 파일
     * @param artworkDTOStr  ArtworkDTO를 포함한 JSON 문자열
     * @return 생성된 ArtworkDTO 객체의 ResponseEntity
     * @throws URISyntaxException URI 구문 예외가 발생할 경우
     * @throws IOException        입출력 예외가 발생할 경우
     */
    @PostMapping("/admin")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<ArtworkDTO> createArtwork(@RequestParam("image") MultipartFile file, @RequestParam("artworkDTO") String artworkDTOStr) throws URISyntaxException, IOException {
        ArtworkDTO artworkDTO = convertToDTO(artworkDTOStr);

        log.debug("REST request to save Artwork : {}", artworkDTO);

        String token = this.validateAndGetToken();

        MemberDTO memberDTO = this.createMember(token);
        artworkDTO.setMember(memberDTO);

        this.uploadImage(file, artworkDTO);

        ArtworkDTO result = this.artworkUsecase.save(artworkDTO);
        return ResponseEntity
            .created(new URI("/api/artworks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }



    /**
     * 업로드된 이미지 파일을 처리하여 ArtworkDTO에 이미지 URL을 설정합니다.
     *
     * @param file       업로드된 이미지 파일
     * @param artworkDTO ArtworkDTO 객체
     * @throws BadRequestAlertException 파일 업로드 실패 시 발생하는 예외
     */
    private void uploadImage(MultipartFile file, ArtworkDTO artworkDTO) {
        log.debug("REST request to upload image file : {}", file);
        if (!Objects.isNull(file)) {
            try {
                String imageUrl = gcsService.uploadImageToGCS(file);
                artworkDTO.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new BadRequestAlertException("File upload failed", ENTITY_NAME, "filereadfailed");
            }
        }
    }


    /**
     * ArtworkDTO를 JSON 문자열 표현에서 실제 ArtworkDTO 객체로 변환합니다.
     *
     * @param artworkDTOStr ArtworkDTO의 JSON 문자열 표현
     * @return ArtworkDTO 객체
     * @throws JsonProcessingException JSON 처리 중 오류가 발생한 경우
     */
    private ArtworkDTO convertToDTO(String artworkDTOStr) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        return mapper.readValue(artworkDTOStr, ArtworkDTO.class);
    }



    /**
     * 주어진 토큰을 사용하여 MemberDTO 객체를 생성합니다.
     *
     * @param token JWT 토큰
     * @return MemberDTO 객체
     */
    private MemberDTO createMember(String token) {  /*TODO -REFACTOR*/
        Authentication authentication = this.tokenProvider.getAuthentication(token);
        Long userId = this.tokenProvider.getUserIdFromToken(token);
        return new MemberDTO(userId,  authentication.getName());
    }

    /**
     * 주어진 id와 ArtworkDTO의 id를 검증합니다.
     *
     * @param id           검증할 id
     * @param artworkDTO   검증할 ArtworkDTO 객체
     * @throws BadRequestAlertException ArtworkDTO의 id가 null인 경우 또는 주어진 id와 ArtworkDTO의 id가 다른 경우 발생합니다.
     */
    private void validateId(Long id, ArtworkDTO artworkDTO) {
        if (artworkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, artworkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
    }

    /**
     * 주어진 id에 해당하는 Artwork가 존재하는지 검증합니다.
     *
     * @param id 검증할 Artwork의 식별자(ID)
     * @return 주어진 id에 해당하는 Artwork 객체
     * @throws BadRequestAlertException Artwork가 존재하지 않는 경우 발생합니다.
     */
    private Artwork validateArtworkExists(Long id) {
        return artworkRepository.findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
    }

    /**
     * 현재 사용자로부터 얻은 JWT 토큰을 유효성 검사하고 유효한 토큰을 반환합니다.
     *
     * @return 유효한 JWT 토큰
     * @throws BadRequestAlertException JWT 토큰이 잘못되었거나 존재하지 않는 경우
     */
    private String validateAndGetToken() {/*TODO -REFACTOR*/
        Optional<String> optToken = SecurityUtils.getCurrentUserJWT();
        if (optToken.isEmpty() || !this.tokenProvider.validateToken(optToken.get())) {
            throw new BadRequestAlertException("Invalid JWT token", ENTITY_NAME, "invalidtoken");
        }
        return optToken.get();
    }


    /**
     * 주어진 Artwork의 소유권을 현재 사용자의 소유권과 비교하여 검증합니다.
     *
     * @param artwork 소유권을 검증할 Artwork 객체
     * @throws BadRequestAlertException 현재 사용자가 Artwork의 소유자가 아닌 경우 발생합니다.
     */
    private void validateOwnership(Artwork artwork) {
        String token = this.validateAndGetToken();
        MemberDTO memberDTO = this.createMember(token);

        if (!artwork.getMember().getId().equals(memberDTO.getId())) {
            throw new BadRequestAlertException("You are not the owner of this artwork", ENTITY_NAME, "notowner");
        }
    }


    /**
     * 작품의 소유자 또는 관리자 여부를 검증합니다.
     *
     * @param artwork 작품 객체
     * @throws BadRequestAlertException 소유자가 아니거나 관리자 권한이 없는 경우 발생하는 예외
     */
    private void validateOwnershipOrAdmin(Artwork artwork) {
        String token = this.validateAndGetToken();
        MemberDTO memberDTO = this.createMember(token);

        if (!artwork.getMember().getId().equals(memberDTO.getId()) || !SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            throw new BadRequestAlertException("작품의 소유자가 아니거나 관리자 권한이 없습니다", ENTITY_NAME, "notowner");
        }
    }
}
