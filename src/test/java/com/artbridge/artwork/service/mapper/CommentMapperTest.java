package com.artbridge.artwork.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.artbridge.artwork.application.service.mapper.CommentMapper;
import org.junit.jupiter.api.BeforeEach;

class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    public void setUp() {
        commentMapper = new CommentMapperImpl();
    }
}
