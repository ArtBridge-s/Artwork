package com.artbridge.artwork.service.mapper;

import com.artbridge.artwork.domain.Artwork;
import com.artbridge.artwork.domain.Comment;
import com.artbridge.artwork.service.dto.ArtworkDTO;
import com.artbridge.artwork.service.dto.CommentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "artwork", source = "artwork", qualifiedByName = "artworkId")
    CommentDTO toDto(Comment s);

    @Named("artworkId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ArtworkDTO toDtoArtworkId(Artwork artwork);
}
