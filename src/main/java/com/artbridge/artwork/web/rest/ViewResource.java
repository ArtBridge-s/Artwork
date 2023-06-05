package com.artbridge.artwork.web.rest;

import com.artbridge.artwork.domain.model.View;
import com.artbridge.artwork.repository.ViewRepository;
import com.artbridge.artwork.service.ViewService;
import com.artbridge.artwork.service.dto.ViewDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link View}.
 */
@RestController
@RequestMapping("/api")
public class ViewResource {

    private final Logger log = LoggerFactory.getLogger(ViewResource.class);

    private static final String ENTITY_NAME = "artworkView";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ViewService viewService;

    private final ViewRepository viewRepository;

    public ViewResource(ViewService viewService, ViewRepository viewRepository) {
        this.viewService = viewService;
        this.viewRepository = viewRepository;
    }

    /**
     * {@code POST  /views} : Create a new view.
     *
     * @param viewDTO the viewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new viewDTO, or with status {@code 400 (Bad Request)} if the view has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/views")
    public ResponseEntity<ViewDTO> createView(@RequestBody ViewDTO viewDTO) throws URISyntaxException {
        log.debug("REST request to save View : {}", viewDTO);
        if (viewDTO.getId() != null) {
            throw new BadRequestAlertException("A new view cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ViewDTO result = viewService.save(viewDTO);
        return ResponseEntity
            .created(new URI("/api/views/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /views/:id} : Updates an existing view.
     *
     * @param id the id of the viewDTO to save.
     * @param viewDTO the viewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated viewDTO,
     * or with status {@code 400 (Bad Request)} if the viewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the viewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/views/{id}")
    public ResponseEntity<ViewDTO> updateView(@PathVariable(value = "id", required = false) final Long id, @RequestBody ViewDTO viewDTO)
        throws URISyntaxException {
        log.debug("REST request to update View : {}, {}", id, viewDTO);
        if (viewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, viewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!viewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ViewDTO result = viewService.update(viewDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, viewDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /views/:id} : Partial updates given fields of an existing view, field will ignore if it is null
     *
     * @param id the id of the viewDTO to save.
     * @param viewDTO the viewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated viewDTO,
     * or with status {@code 400 (Bad Request)} if the viewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the viewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the viewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/views/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ViewDTO> partialUpdateView(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ViewDTO viewDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update View partially : {}, {}", id, viewDTO);
        if (viewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, viewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!viewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ViewDTO> result = viewService.partialUpdate(viewDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, viewDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /views} : get all the views.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of views in body.
     */
    @GetMapping("/views")
    public ResponseEntity<List<ViewDTO>> getAllViews(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Views");
        Page<ViewDTO> page = viewService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /views/:id} : get the "id" view.
     *
     * @param id the id of the viewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the viewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/views/{id}")
    public ResponseEntity<ViewDTO> getView(@PathVariable Long id) {
        log.debug("REST request to get View : {}", id);
        Optional<ViewDTO> viewDTO = viewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(viewDTO);
    }

    /**
     * {@code DELETE  /views/:id} : delete the "id" view.
     *
     * @param id the id of the viewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/views/{id}")
    public ResponseEntity<Void> deleteView(@PathVariable Long id) {
        log.debug("REST request to delete View : {}", id);
        viewService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
