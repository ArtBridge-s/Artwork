package com.artbridge.artwork.application.usecase;

import com.artbridge.artwork.domain.model.Artwork;
import com.artbridge.artwork.application.dto.ArtworkDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Artwork}.
 */
public interface ArtworkUsecase {
    /**
     * 주어진 ArtworkDTO를 저장 요청 합니다.
     *
     * @param artworkDTO 저장 요청할 ArtworkDTO 객체
     * @return 저장 요청된 ArtworkDTO 객체
     */
    ArtworkDTO saveRequest(ArtworkDTO artworkDTO);


    /**
     * Updates a artwork.
     *
     * @param artworkDTO the entity to update.
     * @return the persisted entity.
     */
    ArtworkDTO update(ArtworkDTO artworkDTO);


    /**
     * Partially updates a artwork.
     *
     * @param artworkDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ArtworkDTO> partialUpdate(ArtworkDTO artworkDTO);


    /**
     * Get all the artworks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ArtworkDTO> findAll(Pageable pageable);


    /**
     * Get the "id" artwork.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ArtworkDTO> findOne(Long id);


    /**
     * Delete the "id" artwork.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);



    /**
     *보류 중인 Artwork의 페이지를 검색합니다.
     *
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기, 정렬)가 포함된 객체
     * @return ArtworkDTO의 페이지 객체
     * @throws IllegalArgumentException pageable 매개변수가 null인 경우 발생합니다.
     */
    Page<ArtworkDTO> findCreatePendings(Pageable pageable);


    /**
     * 업데이트 대기 중인 Artwork 목록을 페이지별로 조회합니다.
     *
     * @param pageable 페이지 정보 (Pageable)
     * @return 페이지별로 조회된 업데이트 대기 중인 Artwork 목록 (Page 객체)
     */
    Page<ArtworkDTO> findUpdatePendings(Pageable pageable);


    /**
     * 삭제 대기 중인 Artwork 목록을 페이지별로 조회합니다.
     *
     * @param pageable 페이지 정보 (Pageable)
     * @return 페이지별로 조회된 삭제 대기 중인 Artwork 목록 (Page 객체)
     */
    Page<ArtworkDTO> findDeletePendings(Pageable pageable);



    /**
     * Artwork를 삭제 대기 상태로 변경합니다.
     *
     * @param artworkDTO 삭제 대기 상태로 변경할 Artwork의 정보를 담은 ArtworkDTO 객체
     * @return 업데이트된 Artwork의 정보를 담은 ArtworkDTO 객체
     */
    ArtworkDTO deletePending(ArtworkDTO artworkDTO);



    /**
     * Artwork를 승인 처리합니다.
     *
     * @param id 승인할 Artwork의 식별자(ID)
     * @return 승인된 Artwork의 정보를 담은 ArtworkDTO 객체
     */
    ArtworkDTO authorizeOkArtwork(Long id);


    /**
     * 주어진 ArtworkDTO를 저장합니다.
     *
     * @param artworkDTO 저장할 ArtworkDTO 객체
     * @return 저장된 ArtworkDTO 객체
     */
    ArtworkDTO save(ArtworkDTO artworkDTO);

    void modifyMemberName(long id, String name);
}
