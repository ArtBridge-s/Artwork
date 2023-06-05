package com.artbridge.artwork.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.artbridge.artwork.application.mapper.ArtworkMapper;
import org.junit.jupiter.api.BeforeEach;

class ArtworkMapperTest {

    private ArtworkMapper artworkMapper;

    @BeforeEach
    public void setUp() {
        artworkMapper = new ArtworkMapperImpl();
    }
}
