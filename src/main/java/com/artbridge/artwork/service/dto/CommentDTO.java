package com.artbridge.artwork.service.dto;

import java.io.Serializable;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommentDTO)) {
            return false;
        }

        CommentDTO commentDTO = (CommentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, commentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommentDTO{" +
            "id=" + getId() +
            ", member=" + getMember() +
            ", content='" + getContent() + "'" +
            ", artwork=" + getArtwork() +
            "}";
    }
}
