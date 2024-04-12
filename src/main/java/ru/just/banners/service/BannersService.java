package ru.just.banners.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.just.banners.dto.BannerDto;
import ru.just.banners.dto.BannerIdDto;
import ru.just.banners.dto.CreateBannerDto;
import ru.just.banners.repository.BannersRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BannersService {
    private final BannersRepository bannersRepository;

    public BannerDto findBannerByFeatureAndTag(Long featureId, Long tagId, Boolean useLastRevision) {
        return new BannerDto(bannersRepository.findBannerByFeatureAndTag(featureId, tagId, useLastRevision));
    }

    public BannerDto findBanners(Optional<Long> featureId, Optional<Long> tagId, Integer offset, Integer limit) {
        return bannersRepository.findBanners(featureId, tagId, offset, limit);
    }

    public BannerIdDto createBanner(CreateBannerDto createBannerDto) {
        return bannersRepository.createBanner(createBannerDto);
    }

    public BannerIdDto patchBanner(Long bannerId, CreateBannerDto patchBannerDto) {
        return bannersRepository.patchBanner(bannerId, patchBannerDto);
    }

    public void deleteBanner(Long bannerId) {
        bannersRepository.deleteBannerById(bannerId);
    }
}
