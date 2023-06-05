package com.artbridge.artwork.application.mapper;

import com.artbridge.artwork.application.dto.CommentDTO;
import com.artbridge.artwork.domain.model.Comment;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "artwork", source = "artwork")
    @Mapping(target = "member", source = "member")
    CommentDTO toDto(Comment s);

    @Mapping(target = "artwork", source = "artwork")
    @Mapping(target = "member", source = "member")
    Comment toEntity(CommentDTO commentDTO);
}
