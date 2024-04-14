package ru.just.banners.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.just.banners.dto.BannerDto;
import ru.just.banners.dto.ContentBannerDto;
import ru.just.banners.dto.BannerIdDto;
import ru.just.banners.dto.CreateBannerDto;
import ru.just.banners.model.dao.BannerRecord;
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

    public BannerIdDto createBanner(CreateBannerDto createBannerDto) {
        return new BannerIdDto(bannersRepository.createBanner(createBannerDto));
    }

    public BannerIdDto patchBanner(Long bannerId, CreateBannerDto patchBannerDto) {
        return bannersRepository.patchBanner(bannerId, patchBannerDto);
    }

    public void deleteBanner(Long bannerId) {
        bannersRepository.deleteBannerById(bannerId);
    }
}
