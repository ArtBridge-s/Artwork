package com.artbridge.artwork.service.mapper;

import com.artbridge.artwork.domain.Artwork;
import com.artbridge.artwork.domain.Like;
import com.artbridge.artwork.service.dto.ArtworkDTO;
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

    @InheritInverseConfiguration
    Like toEntity(LikeDTO likeDTO);
}
