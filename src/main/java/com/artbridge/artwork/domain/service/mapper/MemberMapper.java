package com.artbridge.artwork.domain.service.mapper;

import com.artbridge.artwork.domain.vo.Member;
import com.artbridge.artwork.domain.service.dto.MemberDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the value object {@link Member} and its DTO {@link MemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface MemberMapper extends EntityMapper<MemberDTO, Member> {}
