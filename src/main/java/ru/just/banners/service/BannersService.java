package ru.just.banners.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.just.banners.controller.advice.FeatureTagNotUniqueException;
import ru.just.banners.dto.*;
import ru.just.banners.model.dao.BannerAuditModel;
import ru.just.banners.model.dao.BannerFullModel;
import ru.just.banners.model.dao.BannerRecord;
import ru.just.banners.repository.BannersRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BannersService {
    private final BannersRepository bannersRepository;

    @Cacheable(cacheNames = "banner", condition = "!#useLastRevision.booleanValue()")
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
        if (!bannersRepository.isFeatureTagPairsUnique(createBannerDto.getFeatureId(), createBannerDto.getTagIds())) {
            throw new FeatureTagNotUniqueException(createBannerDto.getFeatureId(), createBannerDto.getTagIds());
        }
        return new BannerIdDto(bannersRepository.createBanner(createBannerDto));
    }

    @Transactional
    public void patchBanner(Long bannerId, PatchBannerDto patchBannerDto) {
        bannersRepository.patchBanner(new BannerFullModel(bannerId, patchBannerDto));
    }

    public void deleteBanner(Long bannerId) {
        bannersRepository.deleteBannerById(bannerId);
    }

    @Transactional
    public BannerDto rollbackBannerToVersion(Long bannerId, Long versionId) {
        BannerAuditModel bannerVersion = bannersRepository.findBannerVersion(versionId);
        if (!bannerVersion.getBannerId().equals(bannerId)) {
            throw new EntityNotFoundException("Указанная версия не принадлежит баннеру");
        }
        bannersRepository.patchBanner(new BannerFullModel(bannerVersion));
        return new BannerDto(bannersRepository.findBannerById(bannerId));
    }

    public List<BannerAuditDto> findBannerVersions(Long bannerId) {
        return bannersRepository.findBannerVersions(bannerId).stream()
                .map(BannerAuditDto::new)
                .toList();
    }
}
