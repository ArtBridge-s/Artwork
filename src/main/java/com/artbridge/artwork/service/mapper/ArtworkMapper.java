package com.artbridge.artwork.service.mapper;

import com.artbridge.artwork.domain.Artwork;
import com.artbridge.artwork.service.dto.ArtworkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Artwork} and its DTO {@link ArtworkDTO}.
 */
@Mapper(componentModel = "spring")
public interface ArtworkMapper extends EntityMapper<ArtworkDTO, Artwork> {

    @Mapping(target = "member", source = "member")
    ArtworkDTO toDto(Artwork artwork);

    @Mapping(target = "member", source = "member")
    Artwork toEntity(ArtworkDTO artworkDTO);

}
