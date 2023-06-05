package com.artbridge.artwork.domain.repository;

import com.artbridge.artwork.domain.model.Artwork;
import com.artbridge.artwork.domain.standardType.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the Artwork entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    Page<Artwork> findAllByStatus(Status status, Pageable pageable);

    Optional<Artwork> findByIdAndStatus(Long id, Status status);

    Set<Artwork> findAllByMemberId(Long id);
}
