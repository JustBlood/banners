package ru.just.banners.model.domain;

import jakarta.persistence.Column;
import lombok.Data;
import ru.just.banners.dto.CreateBannerDto;

import java.util.List;

@Data
public class BannerModel {
    @Column(name = "BANNER_ID")
    private Long bannerId;
    @Column(name = "FEATURE_ID")
    private Long featureId;
    private List<Long> tagIds;
    @Column(name = "CONTENT")
    private String content;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    public BannerModel(Long bannerId, CreateBannerDto createBannerDto) {
        this.bannerId = bannerId;
        this.featureId = createBannerDto.getFeatureId();
        this.tagIds = createBannerDto.getTagIds();
        this.content = createBannerDto.getContent();
        this.isActive = createBannerDto.getIsActive();
    }

    public BannerModel() {
    }
}
