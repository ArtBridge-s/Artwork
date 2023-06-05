package com.artbridge.artwork.application.usecase.impl;

import com.artbridge.artwork.application.usecase.ArtworkUsecase;
import com.artbridge.artwork.infrastructure.messaging.MemberInPort;
import com.artbridge.artwork.infrastructure.messaging.MemberProducer;
import com.artbridge.artwork.domain.model.Artwork;
import com.artbridge.artwork.domain.standardType.Status;
import com.artbridge.artwork.infrastructure.repository.ArtworkRepository;
import com.artbridge.artwork.application.dto.ArtworkDTO;
import com.artbridge.artwork.application.mapper.ArtworkMapper;
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
public class ArtworkUsecaseImpl implements ArtworkUsecase, MemberInPort {

    private final Logger log = LoggerFactory.getLogger(ArtworkUsecaseImpl.class);

    private final ArtworkRepository artworkRepository;

    private final ArtworkMapper artworkMapper;

    private final MemberProducer memberProducer;

    public ArtworkUsecaseImpl(ArtworkRepository artworkRepository, ArtworkMapper artworkMapper, MemberProducer memberProducer) {
        this.artworkRepository = artworkRepository;
        this.artworkMapper = artworkMapper;
        this.memberProducer = memberProducer;
    }

    @Override
    public ArtworkDTO saveRequest(ArtworkDTO artworkDTO) {
        log.debug("Request to save Artwork : {}", artworkDTO);
        this.memberProducer.requestMemberName(artworkDTO.getMember().getId());

        Artwork artwork = artworkMapper.toEntity(artworkDTO);
        artwork.setStatus(Status.UPLOAD_PENDING);
        artwork = artworkRepository.save(artwork);
        return artworkMapper.toDto(artwork);
    }

    @Override
    public ArtworkDTO update(ArtworkDTO artworkDTO) {
        log.debug("Request to update Artwork : {}", artworkDTO);
        Artwork artwork = artworkMapper.toEntity(artworkDTO);
        artwork.setStatus(Status.REVISION_PENDING);
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
        return artworkRepository.findAllByStatus(Status.OK, pageable).map(artworkMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArtworkDTO> findOne(Long id) {
        log.debug("Request to get Artwork : {}", id);
        return artworkRepository.findByIdAndStatus(id, Status.OK).map(artworkMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Artwork : {}", id);
        artworkRepository.deleteById(id);
    }

    @Override
    public Page<ArtworkDTO> findCreatePendings(Pageable pageable) {
        log.debug("Request to get all Artworks");
        return artworkRepository.findAllByStatus(Status.UPLOAD_PENDING, pageable).map(artworkMapper::toDto);
    }

    @Override
    public Page<ArtworkDTO> findUpdatePendings(Pageable pageable) {
        log.debug("Request to get all Artworks");
        return artworkRepository.findAllByStatus(Status.REVISION_PENDING, pageable).map(artworkMapper::toDto);
    }

    @Override
    public Page<ArtworkDTO> findDeletePendings(Pageable pageable) {
        log.debug("Request to get all Artworks");
        return artworkRepository.findAllByStatus(Status.DELETE_PENDING, pageable).map(artworkMapper::toDto);
    }

    @Override
    public ArtworkDTO deletePending(ArtworkDTO artworkDTO) {
        log.debug("Request to delete Artwork : {}", artworkDTO);
        Artwork artwork = artworkMapper.toEntity(artworkDTO);
        artwork.setStatus(Status.DELETE_PENDING);
        artwork = artworkRepository.save(artwork);
        return artworkMapper.toDto(artwork);
    }

    @Override
    public ArtworkDTO authorizeOkArtwork(Long id) {
        log.debug("Request to authorize Artwork : {}", id);
        return artworkRepository.findById(id)
            .map(artwork -> {
                artwork.setStatus(Status.OK);
                return artworkMapper.toDto(artworkRepository.save(artwork));
            })
            .orElseThrow();
    }

    @Override
    public ArtworkDTO save(ArtworkDTO artworkDTO) {
        log.debug("Request to save Artwork : {}", artworkDTO);
        this.memberProducer.requestMemberName(artworkDTO.getMember().getId());

        Artwork artwork = artworkMapper.toEntity(artworkDTO);
        artwork.setStatus(Status.OK);
        artwork = artworkRepository.save(artwork);
        return artworkMapper.toDto(artwork);
    }

    @Override
    public void updateMemberName(Long id, String name) {
        log.debug("Request to update Artwork : {}", id);
        artworkRepository.findAllByMemberId(id).forEach(artwork -> {
            artwork.getMember().setName(name);
            artworkRepository.save(artwork);
        });
    }
}
