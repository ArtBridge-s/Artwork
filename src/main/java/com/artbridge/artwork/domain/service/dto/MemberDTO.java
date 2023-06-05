package com.artbridge.artwork.domain.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberDTO implements Serializable {
    private Long id;
    private String login;
    private String name;

    public MemberDTO(Long userId, String login) {
        this.id = userId;
        this.login = login;
    }
}
