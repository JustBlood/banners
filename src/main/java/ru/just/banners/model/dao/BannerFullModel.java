package ru.just.banners.model.dao;

import jakarta.persistence.Column;
import lombok.Data;
import ru.just.banners.dto.CreateBannerDto;

import java.util.List;

@Data
public class BannerFullModel {
    @Column(name = "BANNER_ID")
    private Long bannerId;
    @Column(name = "FEATURE_ID")
    private Long featureId;
    @Column(name = "TAGS")
    private List<Long> tagIds;
    @Column(name = "CONTENT")
    private String content;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    public BannerFullModel(Long bannerId, CreateBannerDto createBannerDto) {
        this.bannerId = bannerId;
        this.featureId = createBannerDto.getFeatureId();
        this.tagIds = createBannerDto.getTagIds();
        this.content = createBannerDto.getContent();
        this.isActive = createBannerDto.getIsActive();
    }

    public BannerFullModel(BannerAuditModel bannerVersion) {
        this.bannerId = bannerVersion.getBannerId();
        this.featureId = bannerVersion.getFeatureId();
        this.tagIds = bannerVersion.getTagIds();
        this.content = bannerVersion.getContent();
        this.isActive = bannerVersion.getIsActive();
    }
}
