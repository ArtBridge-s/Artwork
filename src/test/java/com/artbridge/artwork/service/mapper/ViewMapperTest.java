package com.artbridge.artwork.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.artbridge.artwork.application.service.mapper.ViewMapper;
import org.junit.jupiter.api.BeforeEach;

class ViewMapperTest {

    private ViewMapper viewMapper;

    @BeforeEach
    public void setUp() {
        viewMapper = new ViewMapperImpl();
    }
}
