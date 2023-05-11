package com.artbridge.artwork.service.dto;

import com.artbridge.artwork.domain.enumeration.Status;
import java.io.Serializable;
import java.util.Objects;
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

    private Status status;

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArtworkDTO)) {
            return false;
        }

        ArtworkDTO artworkDTO = (ArtworkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, artworkDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ArtworkDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", shortDescription='" + getShortDescription() + "'" +
            ", longDescription='" + getLongDescription() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", artistname='" + getArtistname() + "'" +
            ", makingday='" + getMakingday() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
