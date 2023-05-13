package com.artbridge.artwork.service.mapper;

import com.artbridge.artwork.domain.valueobject.Member;
import com.artbridge.artwork.service.dto.MemberDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the value object {@link Member} and its DTO {@link MemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface MemberMapper extends EntityMapper<MemberDTO, Member> {}
