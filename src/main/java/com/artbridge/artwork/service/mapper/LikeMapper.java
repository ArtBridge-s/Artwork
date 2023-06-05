package com.artbridge.artwork.service.mapper;

import com.artbridge.artwork.domain.model.Like;
import com.artbridge.artwork.service.dto.LikeDTO;
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
