package ru.just.banners.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.just.banners.dto.BannerDto;
import ru.just.banners.dto.ContentBannerDto;
import ru.just.banners.dto.BannerIdDto;
import ru.just.banners.dto.CreateBannerDto;
import ru.just.banners.service.BannersService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
@Slf4j
public class BannersController {
    private final BannersService bannersService;

    @GetMapping("/user_banner")
    public ResponseEntity<ContentBannerDto> findUserBanner(@RequestParam("tag_id") Long tagId,
                                                           @RequestParam("feature_id") Long featureId,
                                                           @RequestParam(value = "use_last_revision", required = false)
                                                    Optional<Boolean> useLastRevision) {
        ContentBannerDto contentBannerDto = bannersService.findBannerByFeatureAndTag(featureId, tagId, useLastRevision.orElse(false));
        return new ResponseEntity<>(contentBannerDto, HttpStatus.OK);
    }

    @GetMapping("/banner")
    public ResponseEntity<List<BannerDto>> findBanners(@RequestParam(value = "feature_id", required = false)
                                                 Optional<Long> featureId,
                                                 @RequestParam(value = "tag_id", required = false)
                                                 Optional<Long> tagId,
                                                 @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                 @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        List<BannerDto> bannerDto = bannersService.findBanners(featureId, tagId, offset, limit);
        return new ResponseEntity<>(bannerDto, HttpStatus.OK);
    }

    @PostMapping("/banner")
    public ResponseEntity<BannerIdDto> findBanners(@RequestBody CreateBannerDto createBannerDto) {
        BannerIdDto bannerDto = bannersService.createBanner(createBannerDto);
        return new ResponseEntity<>(bannerDto, HttpStatus.OK);
    }

    @PatchMapping("/banner/{bannerId}")
    public ResponseEntity<Void> findBanners(@PathVariable Long bannerId,
                                            @RequestBody CreateBannerDto patchBannerDto) {
        bannersService.patchBanner(bannerId, patchBannerDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/banner/{bannerId}")
    public ResponseEntity<Void> findBanners(@PathVariable Long bannerId) {
        bannersService.deleteBanner(bannerId);
        return ResponseEntity.ok().build();
    }
}
