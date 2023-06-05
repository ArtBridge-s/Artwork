package com.artbridge.artwork.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.artbridge.artwork.IntegrationTest;
import com.artbridge.artwork.domain.model.View;
import com.artbridge.artwork.infrastructure.repository.ViewRepository;
import com.artbridge.artwork.presentation.rest.ViewResource;
import com.artbridge.artwork.service.dto.ViewDTO;
import com.artbridge.artwork.service.mapper.ViewMapper;
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
 * Integration tests for the {@link ViewResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ViewResourceIT {

    private static final Long DEFAULT_MEMBER = 1L;
    private static final Long UPDATED_MEMBER = 2L;

    private static final String ENTITY_API_URL = "/api/views";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private ViewMapper viewMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restViewMockMvc;

    private View view;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static View createEntity(EntityManager em) {
        View view = new View().member(DEFAULT_MEMBER);
        return view;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static View createUpdatedEntity(EntityManager em) {
        View view = new View().member(UPDATED_MEMBER);
        return view;
    }

    @BeforeEach
    public void initTest() {
        view = createEntity(em);
    }

    @Test
    @Transactional
    void createView() throws Exception {
        int databaseSizeBeforeCreate = viewRepository.findAll().size();
        // Create the View
        ViewDTO viewDTO = viewMapper.toDto(view);
        restViewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(viewDTO)))
            .andExpect(status().isCreated());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeCreate + 1);
        View testView = viewList.get(viewList.size() - 1);
        assertThat(testView.getMember()).isEqualTo(DEFAULT_MEMBER);
    }

    @Test
    @Transactional
    void createViewWithExistingId() throws Exception {
        // Create the View with an existing ID
        view.setId(1L);
        ViewDTO viewDTO = viewMapper.toDto(view);

        int databaseSizeBeforeCreate = viewRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restViewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(viewDTO)))
            .andExpect(status().isBadRequest());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllViews() throws Exception {
        // Initialize the database
        viewRepository.saveAndFlush(view);

        // Get all the viewList
        restViewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(view.getId().intValue())))
            .andExpect(jsonPath("$.[*].member").value(hasItem(DEFAULT_MEMBER.intValue())));
    }

    @Test
    @Transactional
    void getView() throws Exception {
        // Initialize the database
        viewRepository.saveAndFlush(view);

        // Get the view
        restViewMockMvc
            .perform(get(ENTITY_API_URL_ID, view.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(view.getId().intValue()))
            .andExpect(jsonPath("$.member").value(DEFAULT_MEMBER.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingView() throws Exception {
        // Get the view
        restViewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingView() throws Exception {
        // Initialize the database
        viewRepository.saveAndFlush(view);

        int databaseSizeBeforeUpdate = viewRepository.findAll().size();

        // Update the view
        View updatedView = viewRepository.findById(view.getId()).get();
        // Disconnect from session so that the updates on updatedView are not directly saved in db
        em.detach(updatedView);
        updatedView.member(UPDATED_MEMBER);
        ViewDTO viewDTO = viewMapper.toDto(updatedView);

        restViewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, viewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(viewDTO))
            )
            .andExpect(status().isOk());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeUpdate);
        View testView = viewList.get(viewList.size() - 1);
        assertThat(testView.getMember()).isEqualTo(UPDATED_MEMBER);
    }

    @Test
    @Transactional
    void putNonExistingView() throws Exception {
        int databaseSizeBeforeUpdate = viewRepository.findAll().size();
        view.setId(count.incrementAndGet());

        // Create the View
        ViewDTO viewDTO = viewMapper.toDto(view);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restViewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, viewDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(viewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchView() throws Exception {
        int databaseSizeBeforeUpdate = viewRepository.findAll().size();
        view.setId(count.incrementAndGet());

        // Create the View
        ViewDTO viewDTO = viewMapper.toDto(view);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restViewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(viewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamView() throws Exception {
        int databaseSizeBeforeUpdate = viewRepository.findAll().size();
        view.setId(count.incrementAndGet());

        // Create the View
        ViewDTO viewDTO = viewMapper.toDto(view);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restViewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(viewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateViewWithPatch() throws Exception {
        // Initialize the database
        viewRepository.saveAndFlush(view);

        int databaseSizeBeforeUpdate = viewRepository.findAll().size();

        // Update the view using partial update
        View partialUpdatedView = new View();
        partialUpdatedView.setId(view.getId());

        restViewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedView.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedView))
            )
            .andExpect(status().isOk());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeUpdate);
        View testView = viewList.get(viewList.size() - 1);
        assertThat(testView.getMember()).isEqualTo(DEFAULT_MEMBER);
    }

    @Test
    @Transactional
    void fullUpdateViewWithPatch() throws Exception {
        // Initialize the database
        viewRepository.saveAndFlush(view);

        int databaseSizeBeforeUpdate = viewRepository.findAll().size();

        // Update the view using partial update
        View partialUpdatedView = new View();
        partialUpdatedView.setId(view.getId());

        partialUpdatedView.member(UPDATED_MEMBER);

        restViewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedView.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedView))
            )
            .andExpect(status().isOk());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeUpdate);
        View testView = viewList.get(viewList.size() - 1);
        assertThat(testView.getMember()).isEqualTo(UPDATED_MEMBER);
    }

    @Test
    @Transactional
    void patchNonExistingView() throws Exception {
        int databaseSizeBeforeUpdate = viewRepository.findAll().size();
        view.setId(count.incrementAndGet());

        // Create the View
        ViewDTO viewDTO = viewMapper.toDto(view);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restViewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, viewDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(viewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchView() throws Exception {
        int databaseSizeBeforeUpdate = viewRepository.findAll().size();
        view.setId(count.incrementAndGet());

        // Create the View
        ViewDTO viewDTO = viewMapper.toDto(view);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restViewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(viewDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamView() throws Exception {
        int databaseSizeBeforeUpdate = viewRepository.findAll().size();
        view.setId(count.incrementAndGet());

        // Create the View
        ViewDTO viewDTO = viewMapper.toDto(view);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restViewMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(viewDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the View in the database
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteView() throws Exception {
        // Initialize the database
        viewRepository.saveAndFlush(view);

        int databaseSizeBeforeDelete = viewRepository.findAll().size();

        // Delete the view
        restViewMockMvc
            .perform(delete(ENTITY_API_URL_ID, view.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<View> viewList = viewRepository.findAll();
        assertThat(viewList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
