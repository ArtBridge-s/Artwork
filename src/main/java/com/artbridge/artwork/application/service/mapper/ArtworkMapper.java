package com.artbridge.artwork.application.service.mapper;

import com.artbridge.artwork.domain.model.Artwork;
import com.artbridge.artwork.application.service.dto.ArtworkDTO;
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
