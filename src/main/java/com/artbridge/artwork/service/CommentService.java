package com.artbridge.artwork.service;

import com.artbridge.artwork.service.dto.CommentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.artbridge.artwork.domain.Comment}.
 */
public interface CommentService {

    /**
     * Comment를 저장합니다.
     *
     * @param commentDTO 저장할 Comment의 정보를 담은 CommentDTO 객체
     * @return 저장된 Comment의 정보를 담은 CommentDTO 객체
     */
    CommentDTO save(CommentDTO commentDTO);

    /**
     * Updates a comment.
     *
     * @param commentDTO the entity to update.
     * @return the persisted entity.
     */
    CommentDTO update(CommentDTO commentDTO);

    /**
     * Partially updates a comment.
     *
     * @param commentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CommentDTO> partialUpdate(CommentDTO commentDTO);

    /**
     * Get all the comments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CommentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" comment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CommentDTO> findOne(Long id);

    /**
     * Delete the "id" comment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
