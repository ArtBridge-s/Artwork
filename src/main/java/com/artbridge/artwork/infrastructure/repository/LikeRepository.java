package com.artbridge.artwork.infrastructure.repository;

import com.artbridge.artwork.domain.model.Like;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Like entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Long countByArtwork_Id(Long artworkId);

    Boolean existsByArtwork_IdAndMember_Id(Long artworkId, Long memberId);

    void deleteByArtwork_IdAndMember_Id(Long artworkId, Long memberId);

}
