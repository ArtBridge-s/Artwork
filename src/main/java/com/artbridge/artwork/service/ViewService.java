package com.artbridge.artwork.service;

import com.artbridge.artwork.service.dto.ViewDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.artbridge.artwork.domain.View}.
 */
public interface ViewService {
    /**
     * Save a view.
     *
     * @param viewDTO the entity to save.
     * @return the persisted entity.
     */
    ViewDTO save(ViewDTO viewDTO);

    /**
     * Updates a view.
     *
     * @param viewDTO the entity to update.
     * @return the persisted entity.
     */
    ViewDTO update(ViewDTO viewDTO);

    /**
     * Partially updates a view.
     *
     * @param viewDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ViewDTO> partialUpdate(ViewDTO viewDTO);

    /**
     * Get all the views.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ViewDTO> findAll(Pageable pageable);

    /**
     * Get the "id" view.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ViewDTO> findOne(Long id);

    /**
     * Delete the "id" view.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
