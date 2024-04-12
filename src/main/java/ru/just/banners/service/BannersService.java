package ru.just.banners.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.just.banners.dto.BannerDto;
import ru.just.banners.repository.BannersRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class BannersService {
    private final BannersRepository bannersRepository;

    public BannerDto findBannerByFeatureAndTag(Long featureId, Long tagId, Boolean useLastRevision) {
        return new BannerDto(bannersRepository.findBannerByFeatureAndTag(featureId, tagId, useLastRevision));
    }
}
