package com.artbridge.artwork.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ViewMapperTest {

    private ViewMapper viewMapper;

    @BeforeEach
    public void setUp() {
        viewMapper = new ViewMapperImpl();
    }
}
