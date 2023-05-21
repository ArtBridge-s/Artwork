package com.artbridge.artwork.web.rest;

import com.artbridge.artwork.adaptor.GCSService;
import com.artbridge.artwork.adaptor.GCSServiceimpl;
import com.artbridge.artwork.repository.ArtworkRepository;
import com.artbridge.artwork.security.SecurityUtils;
import com.artbridge.artwork.security.jwt.TokenProvider;
import com.artbridge.artwork.service.ArtworkService;
import com.artbridge.artwork.service.dto.ArtworkDTO;
import com.artbridge.artwork.service.dto.MemberDTO;
import com.artbridge.artwork.web.rest.errors.BadRequestAlertException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.artbridge.artwork.domain.Artwork}.
 */
@RestController
@RequestMapping("/api/artworks")
public class ArtworkResource {

    private final Logger log = LoggerFactory.getLogger(ArtworkResource.class);

    private static final String ENTITY_NAME = "artworkArtwork";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ArtworkService artworkService;

    private final ArtworkRepository artworkRepository;

    private final TokenProvider tokenProvider;

    private final GCSService gcsService;

    public ArtworkResource(ArtworkService artworkService, ArtworkRepository artworkRepository, TokenProvider tokenProvider, GCSService gcsService) {
        this.artworkService = artworkService;
        this.artworkRepository = artworkRepository;
        this.tokenProvider = tokenProvider;
        this.gcsService = gcsService;
    }

    /**
     * {@code POST  /artworks} : Create a new artwork.
     *
     * @param artworkDTO the artworkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new artworkDTO, or with status {@code 400 (Bad Request)} if the artwork has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @ApiOperation(value = "작품 업로드 요청")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created", content = @Content(schema = @Schema(implementation = ArtworkDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<ArtworkDTO> createArtwork(@RequestParam("image") MultipartFile file, @RequestBody ArtworkDTO artworkDTO) throws URISyntaxException {
        log.debug("REST request to save Artwork : {}", artworkDTO);
        Optional<String> optToken = SecurityUtils.getCurrentUserJWT();

        if (artworkDTO.getId() != null) {
            throw new BadRequestAlertException("A new artwork cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (optToken.isEmpty()) {
            throw new BadRequestAlertException("Invalid JWT token", ENTITY_NAME, "invalidtoken");
        }
        String token = optToken.get();

        if (!this.tokenProvider.validateToken(token)) {
            throw new BadRequestAlertException("Invalid JWT token", ENTITY_NAME, "invalidtoken");
        }
        Authentication authentication = this.tokenProvider.getAuthentication(token);
        Long userId = this.tokenProvider.getUserIdFromToken(token);

        MemberDTO memberDTO = new MemberDTO(userId,  authentication.getName());
        artworkDTO.setMember(memberDTO);

        this.uploadImage(file, artworkDTO);

        ArtworkDTO result = this.artworkService.save(artworkDTO);
        return ResponseEntity
            .created(new URI("/api/artworks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(this.applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }



    /**
     * {@code PUT  /artworks/:id} : Updates an existing artwork.
     *
     * @param id         the id of the artworkDTO to save.
     * @param artworkDTO the artworkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated artworkDTO,
     * or with status {@code 400 (Bad Request)} if the artworkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the artworkDTO couldn't be updated.
     */
    @ApiOperation(value = "작품 수정")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = ArtworkDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ArtworkDTO> updateArtwork(@PathVariable(value = "id", required = false) final Long id, @RequestBody ArtworkDTO artworkDTO) {
        log.debug("REST request to update Artwork : {}, {}", id, artworkDTO);
        if (artworkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, artworkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!artworkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ArtworkDTO result = artworkService.update(artworkDTO);
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
    @ApiOperation(value = "Partial updates given fields of an existing artwork")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = ArtworkDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping(value = "/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<ArtworkDTO> partialUpdateArtwork(@PathVariable(value = "id", required = false) final Long id, @RequestBody ArtworkDTO artworkDTO) {
        log.debug("REST request to partial update Artwork partially : {}, {}", id, artworkDTO);
        if (artworkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, artworkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!artworkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ArtworkDTO> result = artworkService.partialUpdate(artworkDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, artworkDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /artworks} : get all the artworks.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of artworks in body.
     */
    @ApiOperation(value = "Get all artworks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArtworkDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<ArtworkDTO>> getAllArtworks(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Artworks");
        Page<ArtworkDTO> page = artworkService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /artworks/:id} : get the "id" artwork.
     *
     * @param id the id of the artworkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the artworkDTO, or with status {@code 404 (Not Found)}.
     */
    @ApiOperation(value = "Get an artwork by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = ArtworkDTO.class))),
        @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArtworkDTO> getArtwork(@PathVariable Long id) {
        log.debug("REST request to get Artwork : {}", id);
        Optional<ArtworkDTO> artworkDTO = artworkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(artworkDTO);
    }

    /**
     * {@code DELETE  /artworks/:id} : delete the "id" artwork.
     *
     * @param id the id of the artworkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @ApiOperation(value = "Delete an artwork by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtwork(@PathVariable Long id) {
        log.debug("REST request to delete Artwork : {}", id);
        artworkService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }


    /**
     * 업로드된 이미지 파일을 처리하여 ArtworkDTO에 이미지 URL을 설정합니다.
     *
     * @param file       업로드된 이미지 파일
     * @param artworkDTO ArtworkDTO 객체
     * @throws BadRequestAlertException 파일 업로드 실패 시 발생하는 예외
     */
    private void uploadImage(MultipartFile file, ArtworkDTO artworkDTO) {
        if (!Objects.isNull(file)) {
            try {
                String imageUrl = gcsService.uploadFileToGCS(file);
                artworkDTO.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new BadRequestAlertException("File upload failed", ENTITY_NAME, "filereadfailed");
            }
        }
    }
}
