package com.artbridge.artwork.domain.service.mapper;

import com.artbridge.artwork.domain.model.View;
import com.artbridge.artwork.domain.service.dto.ViewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link View} and its DTO {@link ViewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ViewMapper extends EntityMapper<ViewDTO, View> {
    @Mapping(target = "artwork", source = "artwork")
    @Mapping(target = "member", source = "member")
    ViewDTO toDto(View s);

    @Mapping(target = "artwork", source = "artwork")
    @Mapping(target = "member", source = "member")
    View toEntity(ViewDTO viewDTO);
}
