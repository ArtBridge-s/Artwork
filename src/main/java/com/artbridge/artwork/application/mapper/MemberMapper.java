package com.artbridge.artwork.application.mapper;

import com.artbridge.artwork.domain.vo.Member;
import com.artbridge.artwork.application.dto.MemberDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the value object {@link Member} and its DTO {@link MemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface MemberMapper extends EntityMapper<MemberDTO, Member> {}
