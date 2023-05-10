package com.artbridge.artwork.service.mapper;

import com.artbridge.artwork.domain.Artwork;
import com.artbridge.artwork.domain.View;
import com.artbridge.artwork.service.dto.ArtworkDTO;
import com.artbridge.artwork.service.dto.ViewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link View} and its DTO {@link ViewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ViewMapper extends EntityMapper<ViewDTO, View> {
    @Mapping(target = "artwork", source = "artwork", qualifiedByName = "artworkId")
    ViewDTO toDto(View s);

    @Named("artworkId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ArtworkDTO toDtoArtworkId(Artwork artwork);
}
