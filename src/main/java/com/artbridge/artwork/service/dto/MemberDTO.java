package com.artbridge.artwork.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemberDTO implements Serializable {
    private Long id;
    private String name;
}
