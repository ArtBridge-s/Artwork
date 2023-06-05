package com.artbridge.artwork.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.artbridge.artwork.IntegrationTest;
import com.artbridge.artwork.domain.model.Artwork;
import com.artbridge.artwork.domain.standardType.Status;
import com.artbridge.artwork.domain.repository.ArtworkRepository;
import com.artbridge.artwork.service.dto.ArtworkDTO;
import com.artbridge.artwork.service.mapper.ArtworkMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ArtworkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ArtworkResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LONG_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_LONG_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_ARTISTNAME = "AAAAAAAAAA";
    private static final String UPDATED_ARTISTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_MAKINGDAY = "AAAAAAAAAA";
    private static final String UPDATED_MAKINGDAY = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.UPLOAD_PENDING;
    private static final Status UPDATED_STATUS = Status.REVISION_PENDING;

    private static final String ENTITY_API_URL = "/api/artworks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restArtworkMockMvc;

    private Artwork artwork;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artwork createEntity(EntityManager em) {
        Artwork artwork = new Artwork()
            .title(DEFAULT_TITLE)
            .shortDescription(DEFAULT_SHORT_DESCRIPTION)
            .longDescription(DEFAULT_LONG_DESCRIPTION)
            .imageUrl(DEFAULT_IMAGE_URL)
            .artistname(DEFAULT_ARTISTNAME)
            .makingday(DEFAULT_MAKINGDAY)
            .status(DEFAULT_STATUS);
        return artwork;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artwork createUpdatedEntity(EntityManager em) {
        Artwork artwork = new Artwork()
            .title(UPDATED_TITLE)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .longDescription(UPDATED_LONG_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .artistname(UPDATED_ARTISTNAME)
            .makingday(UPDATED_MAKINGDAY)
            .status(UPDATED_STATUS);
        return artwork;
    }

    @BeforeEach
    public void initTest() {
        artwork = createEntity(em);
    }

    @Test
    @Transactional
    void createArtwork() throws Exception {
        int databaseSizeBeforeCreate = artworkRepository.findAll().size();
        // Create the Artwork
        ArtworkDTO artworkDTO = artworkMapper.toDto(artwork);
        restArtworkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(artworkDTO)))
            .andExpect(status().isCreated());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeCreate + 1);
        Artwork testArtwork = artworkList.get(artworkList.size() - 1);
        assertThat(testArtwork.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testArtwork.getShortDescription()).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
        assertThat(testArtwork.getLongDescription()).isEqualTo(DEFAULT_LONG_DESCRIPTION);
        assertThat(testArtwork.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testArtwork.getArtistname()).isEqualTo(DEFAULT_ARTISTNAME);
        assertThat(testArtwork.getMakingday()).isEqualTo(DEFAULT_MAKINGDAY);
        assertThat(testArtwork.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createArtworkWithExistingId() throws Exception {
        // Create the Artwork with an existing ID
        artwork.setId(1L);
        ArtworkDTO artworkDTO = artworkMapper.toDto(artwork);

        int databaseSizeBeforeCreate = artworkRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restArtworkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(artworkDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllArtworks() throws Exception {
        // Initialize the database
        artworkRepository.saveAndFlush(artwork);

        // Get all the artworkList
        restArtworkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artwork.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].longDescription").value(hasItem(DEFAULT_LONG_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].artistname").value(hasItem(DEFAULT_ARTISTNAME)))
            .andExpect(jsonPath("$.[*].makingday").value(hasItem(DEFAULT_MAKINGDAY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getArtwork() throws Exception {
        // Initialize the database
        artworkRepository.saveAndFlush(artwork);

        // Get the artwork
        restArtworkMockMvc
            .perform(get(ENTITY_API_URL_ID, artwork.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(artwork.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.shortDescription").value(DEFAULT_SHORT_DESCRIPTION))
            .andExpect(jsonPath("$.longDescription").value(DEFAULT_LONG_DESCRIPTION))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL))
            .andExpect(jsonPath("$.artistname").value(DEFAULT_ARTISTNAME))
            .andExpect(jsonPath("$.makingday").value(DEFAULT_MAKINGDAY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingArtwork() throws Exception {
        // Get the artwork
        restArtworkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingArtwork() throws Exception {
        // Initialize the database
        artworkRepository.saveAndFlush(artwork);

        int databaseSizeBeforeUpdate = artworkRepository.findAll().size();

        // Update the artwork
        Artwork updatedArtwork = artworkRepository.findById(artwork.getId()).get();
        // Disconnect from session so that the updates on updatedArtwork are not directly saved in db
        em.detach(updatedArtwork);
        updatedArtwork
            .title(UPDATED_TITLE)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .longDescription(UPDATED_LONG_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .artistname(UPDATED_ARTISTNAME)
            .makingday(UPDATED_MAKINGDAY)
            .status(UPDATED_STATUS);
        ArtworkDTO artworkDTO = artworkMapper.toDto(updatedArtwork);

        restArtworkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, artworkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(artworkDTO))
            )
            .andExpect(status().isOk());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeUpdate);
        Artwork testArtwork = artworkList.get(artworkList.size() - 1);
        assertThat(testArtwork.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testArtwork.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        assertThat(testArtwork.getLongDescription()).isEqualTo(UPDATED_LONG_DESCRIPTION);
        assertThat(testArtwork.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testArtwork.getArtistname()).isEqualTo(UPDATED_ARTISTNAME);
        assertThat(testArtwork.getMakingday()).isEqualTo(UPDATED_MAKINGDAY);
        assertThat(testArtwork.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingArtwork() throws Exception {
        int databaseSizeBeforeUpdate = artworkRepository.findAll().size();
        artwork.setId(count.incrementAndGet());

        // Create the Artwork
        ArtworkDTO artworkDTO = artworkMapper.toDto(artwork);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtworkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, artworkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(artworkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchArtwork() throws Exception {
        int databaseSizeBeforeUpdate = artworkRepository.findAll().size();
        artwork.setId(count.incrementAndGet());

        // Create the Artwork
        ArtworkDTO artworkDTO = artworkMapper.toDto(artwork);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtworkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(artworkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamArtwork() throws Exception {
        int databaseSizeBeforeUpdate = artworkRepository.findAll().size();
        artwork.setId(count.incrementAndGet());

        // Create the Artwork
        ArtworkDTO artworkDTO = artworkMapper.toDto(artwork);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtworkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(artworkDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateArtworkWithPatch() throws Exception {
        // Initialize the database
        artworkRepository.saveAndFlush(artwork);

        int databaseSizeBeforeUpdate = artworkRepository.findAll().size();

        // Update the artwork using partial update
        Artwork partialUpdatedArtwork = new Artwork();
        partialUpdatedArtwork.setId(artwork.getId());

        partialUpdatedArtwork
            .title(UPDATED_TITLE)
            .longDescription(UPDATED_LONG_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .artistname(UPDATED_ARTISTNAME)
            .makingday(UPDATED_MAKINGDAY);

        restArtworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArtwork.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedArtwork))
            )
            .andExpect(status().isOk());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeUpdate);
        Artwork testArtwork = artworkList.get(artworkList.size() - 1);
        assertThat(testArtwork.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testArtwork.getShortDescription()).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
        assertThat(testArtwork.getLongDescription()).isEqualTo(UPDATED_LONG_DESCRIPTION);
        assertThat(testArtwork.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testArtwork.getArtistname()).isEqualTo(UPDATED_ARTISTNAME);
        assertThat(testArtwork.getMakingday()).isEqualTo(UPDATED_MAKINGDAY);
        assertThat(testArtwork.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateArtworkWithPatch() throws Exception {
        // Initialize the database
        artworkRepository.saveAndFlush(artwork);

        int databaseSizeBeforeUpdate = artworkRepository.findAll().size();

        // Update the artwork using partial update
        Artwork partialUpdatedArtwork = new Artwork();
        partialUpdatedArtwork.setId(artwork.getId());

        partialUpdatedArtwork
            .title(UPDATED_TITLE)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .longDescription(UPDATED_LONG_DESCRIPTION)
            .imageUrl(UPDATED_IMAGE_URL)
            .artistname(UPDATED_ARTISTNAME)
            .makingday(UPDATED_MAKINGDAY)
            .status(UPDATED_STATUS);

        restArtworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArtwork.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedArtwork))
            )
            .andExpect(status().isOk());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeUpdate);
        Artwork testArtwork = artworkList.get(artworkList.size() - 1);
        assertThat(testArtwork.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testArtwork.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        assertThat(testArtwork.getLongDescription()).isEqualTo(UPDATED_LONG_DESCRIPTION);
        assertThat(testArtwork.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testArtwork.getArtistname()).isEqualTo(UPDATED_ARTISTNAME);
        assertThat(testArtwork.getMakingday()).isEqualTo(UPDATED_MAKINGDAY);
        assertThat(testArtwork.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingArtwork() throws Exception {
        int databaseSizeBeforeUpdate = artworkRepository.findAll().size();
        artwork.setId(count.incrementAndGet());

        // Create the Artwork
        ArtworkDTO artworkDTO = artworkMapper.toDto(artwork);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArtworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, artworkDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(artworkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchArtwork() throws Exception {
        int databaseSizeBeforeUpdate = artworkRepository.findAll().size();
        artwork.setId(count.incrementAndGet());

        // Create the Artwork
        ArtworkDTO artworkDTO = artworkMapper.toDto(artwork);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(artworkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamArtwork() throws Exception {
        int databaseSizeBeforeUpdate = artworkRepository.findAll().size();
        artwork.setId(count.incrementAndGet());

        // Create the Artwork
        ArtworkDTO artworkDTO = artworkMapper.toDto(artwork);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArtworkMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(artworkDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Artwork in the database
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteArtwork() throws Exception {
        // Initialize the database
        artworkRepository.saveAndFlush(artwork);

        int databaseSizeBeforeDelete = artworkRepository.findAll().size();

        // Delete the artwork
        restArtworkMockMvc
            .perform(delete(ENTITY_API_URL_ID, artwork.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Artwork> artworkList = artworkRepository.findAll();
        assertThat(artworkList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
