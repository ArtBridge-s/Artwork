package com.artbridge.artwork.application.usecase.impl;

import com.artbridge.artwork.application.usecase.LikeUsecase;
import com.artbridge.artwork.application.dto.LikeDTO;
import com.artbridge.artwork.domain.model.Like;
import com.artbridge.artwork.infrastructure.repository.LikeRepository;
import com.artbridge.artwork.application.mapper.LikeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Like}.
 */
@Service
@Transactional
public class LikeUsecaseImpl implements LikeUsecase {

    private final Logger log = LoggerFactory.getLogger(LikeUsecaseImpl.class);

    private final LikeRepository likeRepository;

    private final LikeMapper likeMapper;

    public LikeUsecaseImpl(LikeRepository likeRepository, LikeMapper likeMapper) {
        this.likeRepository = likeRepository;
        this.likeMapper = likeMapper;
    }


    @Override
    public LikeDTO save(LikeDTO likeDTO) {
        log.debug("Request to save Like : {}", likeDTO);
        /*TODO: - Event memberDto name*/
        Like like = likeMapper.toEntity(likeDTO);
        like = likeRepository.save(like);
        return likeMapper.toDto(like);
    }


    @Override
    public LikeDTO update(LikeDTO likeDTO) {
        log.debug("Request to update Like : {}", likeDTO);
        Like like = likeMapper.toEntity(likeDTO);
        like = likeRepository.save(like);
        return likeMapper.toDto(like);
    }


    @Override
    public Optional<LikeDTO> partialUpdate(LikeDTO likeDTO) {
        log.debug("Request to partially update Like : {}", likeDTO);

        return likeRepository
            .findById(likeDTO.getId())
            .map(existingLike -> {
                likeMapper.partialUpdate(existingLike, likeDTO);

                return existingLike;
            })
            .map(likeRepository::save)
            .map(likeMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<LikeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Likes");
        return likeRepository.findAll(pageable).map(likeMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<LikeDTO> findOne(Long id) {
        log.debug("Request to get Like : {}", id);
        return likeRepository.findById(id).map(likeMapper::toDto);
    }


    @Override
    public void delete(Long artworkId, Long memberId) {
        log.debug("Request to delete Like : {}", artworkId);

        if (likeRepository.existsByArtwork_IdAndMember_Id(artworkId, memberId)) {
            likeRepository.deleteByArtwork_IdAndMember_Id(artworkId, memberId);
        }
    }


    @Override
    public Long countByArtworkId(Long artworkId) {
        return likeRepository.countByArtwork_Id(artworkId);
    }
}
