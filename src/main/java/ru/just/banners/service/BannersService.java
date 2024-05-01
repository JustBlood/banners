package ru.just.banners.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.just.banners.dto.*;
import ru.just.banners.model.dao.BannerRecord;
import ru.just.banners.model.domain.BannerModel;
import ru.just.banners.repository.BannersRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BannersService {
    private final BannersRepository bannersRepository;

    // todo: обработка useLastRevision после добавления кеширования
    public ContentBannerDto findBannerByFeatureAndTag(Long featureId, Long tagId, Boolean useLastRevision) {
        BannerRecord bannerRecord = Optional.ofNullable(bannersRepository
                .findBannerByFeatureAndTag(featureId, tagId, useLastRevision))
                .orElseThrow(() -> new EntityNotFoundException("Баннер не найден"));
        return new ContentBannerDto(bannerRecord);
    }

    public List<BannerDto> findBanners(Optional<Long> featureId, Optional<Long> tagId, Integer offset, Integer limit) {
        return bannersRepository.findBanners(featureId, tagId, offset, limit).stream()
                .map(BannerDto::new)
                .toList();
    }

    @Transactional
    public BannerIdDto createBanner(CreateBannerDto createBannerDto) {
        return new BannerIdDto(bannersRepository.createBanner(createBannerDto));
    }

    @Transactional
    public void patchBanner(Long bannerId, PatchBannerDto patchBannerDto) {
        bannersRepository.patchBanner(new BannerModel(bannerId, patchBannerDto));
    }

    public void deleteBanner(Long bannerId) {
        bannersRepository.deleteBannerById(bannerId);
    }
}
