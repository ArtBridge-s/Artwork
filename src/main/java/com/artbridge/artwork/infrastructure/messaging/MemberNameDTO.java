package com.artbridge.artwork.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class MemberNameDTO implements Serializable {
    long id;
}
