package com.artbridge.artwork.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.artbridge.artwork.application.dto.ArtworkDTO;
import com.artbridge.artwork.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ArtworkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ArtworkDTO.class);
        ArtworkDTO artworkDTO1 = new ArtworkDTO();
        artworkDTO1.setId(1L);
        ArtworkDTO artworkDTO2 = new ArtworkDTO();
        assertThat(artworkDTO1).isNotEqualTo(artworkDTO2);
        artworkDTO2.setId(artworkDTO1.getId());
        assertThat(artworkDTO1).isEqualTo(artworkDTO2);
        artworkDTO2.setId(2L);
        assertThat(artworkDTO1).isNotEqualTo(artworkDTO2);
        artworkDTO1.setId(null);
        assertThat(artworkDTO1).isNotEqualTo(artworkDTO2);
    }
}
