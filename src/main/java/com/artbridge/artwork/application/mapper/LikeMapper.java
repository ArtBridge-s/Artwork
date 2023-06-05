package com.artbridge.artwork.application.mapper;

import com.artbridge.artwork.application.dto.LikeDTO;
import com.artbridge.artwork.domain.model.Like;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Like} and its DTO {@link LikeDTO}.
 */
@Mapper(componentModel = "spring")
public interface LikeMapper extends EntityMapper<LikeDTO, Like> {
    @Mapping(target = "artwork", source = "artwork")
    @Mapping(target = "member", source = "member")
    LikeDTO toDto(Like s);

    @Mapping(target = "artwork", source = "artwork")
    @Mapping(target = "member", source = "member")
    Like toEntity(LikeDTO likeDTO);
}
