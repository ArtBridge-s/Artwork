package com.artbridge.artwork.infrastructure.repository;

import com.artbridge.artwork.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Spring Data JPA repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByArtwork_Id(Pageable pageable, Long artworkId);
    boolean existsByArtwork_Id(Long artworkId);

    Set<Comment> findCommentsByMember_Id(long id);
}
