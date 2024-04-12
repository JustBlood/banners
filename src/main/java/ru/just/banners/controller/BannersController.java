package ru.just.banners.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.just.banners.dto.BannerDto;
import ru.just.banners.service.BannersService;

import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
@Slf4j
public class BannersController {
    private final BannersService bannersService;

    @GetMapping("/user_banner")
    public ResponseEntity<BannerDto> findUserBanner(@RequestParam("tag_id") Long tagId,
                                                    @RequestParam("feature_id") Long featureId,
                                                    @RequestParam(value = "use_last_revision", required = false)
                                                    Optional<Boolean> useLastRevision) {
        BannerDto bannerDto = bannersService.findBannerByFeatureAndTag(featureId, tagId, useLastRevision.orElse(false));
        return new ResponseEntity<>(bannerDto, HttpStatus.OK);
    }
}
