package com.artbridge.artwork.service.dto;

import java.io.Serializable;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ViewDTO)) {
            return false;
        }

        ViewDTO viewDTO = (ViewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, viewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ViewDTO{" +
            "id=" + getId() +
            ", member=" + getMember() +
            ", artwork=" + getArtwork() +
            "}";
    }
}
