package com.artbridge.artwork.application.dto;

import java.io.Serializable;

import com.artbridge.artwork.domain.model.Like;
import lombok.Data;

/**
 * A DTO for the {@link Like} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class LikeDTO implements Serializable {

    private Long id;

    private MemberDTO member;

    private ArtworkDTO artwork;

}
