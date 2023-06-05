package com.artbridge.artwork.domain.repository;

import com.artbridge.artwork.domain.model.View;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the View entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ViewRepository extends JpaRepository<View, Long> {}
