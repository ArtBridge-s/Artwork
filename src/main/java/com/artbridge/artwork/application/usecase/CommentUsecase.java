package com.artbridge.artwork.application.usecase;

import com.artbridge.artwork.domain.model.Comment;
import com.artbridge.artwork.application.dto.CommentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Comment}.
 */
public interface CommentUsecase {

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

    /**
     * Artwork ID를 기준으로 Comment를 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지네이션 정보
     * @param artworkId Artwork의 식별자(ID)
     * @return 페이지네이션된 Comment 리스트를 담은 Page 객체
     */
    Page<CommentDTO> findByArtwokId(Pageable pageable, Long artworkId);
}
