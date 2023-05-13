package com.artbridge.artwork.service.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * A DTO for the {@link com.artbridge.artwork.domain.View} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class ViewDTO implements Serializable {

    private Long id;

    private Long member;

    private ArtworkDTO artwork;

}
