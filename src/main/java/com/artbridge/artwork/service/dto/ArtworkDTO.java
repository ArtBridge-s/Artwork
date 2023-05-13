package com.artbridge.artwork.service.dto;

import com.artbridge.artwork.domain.enumeration.Status;
import java.io.Serializable;
import lombok.Data;

/**
 * A DTO for the {@link com.artbridge.artwork.domain.Artwork} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class ArtworkDTO implements Serializable {

    private Long id;
    private String title;
    private String shortDescription;
    private String longDescription;
    private String imageUrl;
    private String artistname;
    private String makingday;
    private MemberDTO member;
    private Status status;

}
