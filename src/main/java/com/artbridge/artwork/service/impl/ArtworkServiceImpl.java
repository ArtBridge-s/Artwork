package com.artbridge.artwork.service.impl;

import com.artbridge.artwork.domain.Artwork;
import com.artbridge.artwork.domain.enumeration.Status;
import com.artbridge.artwork.repository.ArtworkRepository;
import com.artbridge.artwork.service.ArtworkService;
import com.artbridge.artwork.service.dto.ArtworkDTO;
import com.artbridge.artwork.service.mapper.ArtworkMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Artwork}.
 */
@Service
@Transactional
public class ArtworkServiceImpl implements ArtworkService {

    private final Logger log = LoggerFactory.getLogger(ArtworkServiceImpl.class);

    private final ArtworkRepository artworkRepository;

    private final ArtworkMapper artworkMapper;

    public ArtworkServiceImpl(ArtworkRepository artworkRepository, ArtworkMapper artworkMapper) {
        this.artworkRepository = artworkRepository;
        this.artworkMapper = artworkMapper;
    }

    @Override
    public ArtworkDTO save(ArtworkDTO artworkDTO) {
        log.debug("Request to save Artwork : {}", artworkDTO);

        artworkDTO.setStatus(Status.UPLOAD_PENDING);
        /*TODO: - Event memberDto name*/
        Artwork artwork = artworkMapper.toEntity(artworkDTO);
        artwork = artworkRepository.save(artwork);
        return artworkMapper.toDto(artwork);
    }

    @Override
    public ArtworkDTO update(ArtworkDTO artworkDTO) {
        log.debug("Request to update Artwork : {}", artworkDTO);
        Artwork artwork = artworkMapper.toEntity(artworkDTO);
        artwork = artworkRepository.save(artwork);
        return artworkMapper.toDto(artwork);
    }

    @Override
    public Optional<ArtworkDTO> partialUpdate(ArtworkDTO artworkDTO) {
        log.debug("Request to partially update Artwork : {}", artworkDTO);

        return artworkRepository
            .findById(artworkDTO.getId())
            .map(existingArtwork -> {
                artworkMapper.partialUpdate(existingArtwork, artworkDTO);

                return existingArtwork;
            })
            .map(artworkRepository::save)
            .map(artworkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtworkDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Artworks");
        return artworkRepository.findAll(pageable).map(artworkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArtworkDTO> findOne(Long id) {
        log.debug("Request to get Artwork : {}", id);
        return artworkRepository.findById(id).map(artworkMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Artwork : {}", id);
        artworkRepository.deleteById(id);
    }

    @Override
    public Page<ArtworkDTO> findPendingList(Pageable pageable) {
        log.debug("Request to get all Artworks");
        return artworkRepository.findAllByStatus(Status.UPLOAD_PENDING, pageable).map(artworkMapper::toDto);
    }
}
