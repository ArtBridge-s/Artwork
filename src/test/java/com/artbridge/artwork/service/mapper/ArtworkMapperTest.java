package com.artbridge.artwork.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArtworkMapperTest {

    private ArtworkMapper artworkMapper;

    @BeforeEach
    public void setUp() {
        artworkMapper = new ArtworkMapperImpl();
    }
}
