package com.artbridge.artwork.service.mapper;

import com.artbridge.artwork.domain.Comment;
import com.artbridge.artwork.service.dto.CommentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "artwork", source = "artwork")
    @Mapping(target = "member", source = "member")
    CommentDTO toDto(Comment s);

    @InheritInverseConfiguration
    Comment toEntity(CommentDTO commentDTO);
}
