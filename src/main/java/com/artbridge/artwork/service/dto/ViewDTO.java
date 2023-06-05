package com.artbridge.artwork.service.dto;

import java.io.Serializable;

import com.artbridge.artwork.domain.model.View;
import lombok.Data;

/**
 * A DTO for the {@link View} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class ViewDTO implements Serializable {

    private Long id;
    private MemberDTO member;
    private ArtworkDTO artwork;

}
