package com.artbridge.artwork.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.artbridge.artwork.application.mapper.LikeMapper;
import org.junit.jupiter.api.BeforeEach;

class LikeMapperTest {

    private LikeMapper likeMapper;

    @BeforeEach
    public void setUp() {
        likeMapper = new LikeMapperImpl();
    }
}
