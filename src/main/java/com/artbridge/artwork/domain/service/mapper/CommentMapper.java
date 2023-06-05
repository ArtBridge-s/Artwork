package com.artbridge.artwork.domain.service.mapper;

import com.artbridge.artwork.domain.model.Comment;
import com.artbridge.artwork.domain.service.dto.CommentDTO;
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
