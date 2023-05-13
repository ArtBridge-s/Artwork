package com.artbridge.artwork.domain.valueobject;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Member implements Serializable {

    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name")
    private String name;
}
