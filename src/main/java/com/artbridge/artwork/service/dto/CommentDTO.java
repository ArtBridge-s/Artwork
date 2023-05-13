package com.artbridge.artwork.service.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * A DTO for the {@link com.artbridge.artwork.domain.Comment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class CommentDTO implements Serializable {

    private Long id;

    private Long member;

    private String content;

    private ArtworkDTO artwork;

}
