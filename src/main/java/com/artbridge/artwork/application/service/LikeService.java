package com.artbridge.artwork.application.service;

import com.artbridge.artwork.application.service.dto.LikeDTO;
import com.artbridge.artwork.domain.model.Like;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Like}.
 */
public interface LikeService {

    /**
     * Like을 저장합니다.
     *
     * @param likeDTO 저장할 Like의 정보를 담은 LikeDTO 객체
     * @return 저장된 Like의 정보를 담은 LikeDTO 객체
     */
    LikeDTO save(LikeDTO likeDTO);

    /**
     * Updates a like.
     *
     * @param likeDTO the entity to update.
     * @return the persisted entity.
     */
    LikeDTO update(LikeDTO likeDTO);

    /**
     * Partially updates a like.
     *
     * @param likeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LikeDTO> partialUpdate(LikeDTO likeDTO);

    /**
     * Get all the likes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LikeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" like.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LikeDTO> findOne(Long id);


    /**
     * 주어진 Artwork ID와 MemberDTO ID를 기준으로 Like을 삭제합니다.
     *
     * @param artworkId 삭제할 Like의 Artwork ID
     * @param memberId Like을 소유한 MemberDTO의 ID
     */
    void delete(Long artworkId, Long memberId);

    /**
     * Artwork ID를 기준으로 Like 개수를 조회합니다.
     *
     * @param artworkId Artwork의 식별자(ID)
     * @return Artwork ID를 기준으로 조회된 Like의 개수
     */
    Long countByArtworkId(Long artworkId);

}
