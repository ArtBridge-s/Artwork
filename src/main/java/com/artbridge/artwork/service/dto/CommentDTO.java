package com.artbridge.artwork.service.dto;

import java.io.Serializable;

import com.artbridge.artwork.domain.model.Comment;
import lombok.Data;

/**
 * A DTO for the {@link Comment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class CommentDTO implements Serializable {

    private Long id;

    private MemberDTO member;

    private String content;

    private ArtworkDTO artwork;

}
