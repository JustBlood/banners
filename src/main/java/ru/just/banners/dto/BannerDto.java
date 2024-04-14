package ru.just.banners.dto;

import lombok.Getter;
import lombok.Setter;
import ru.just.banners.model.domain.BannerModel;

import java.util.List;

@Getter
@Setter
public class BannerDto {
    private Long bannerId;
    private Long featureId;
    private List<Long> tagIds;
    private String content;

    public BannerDto(BannerModel model) {
        this.bannerId = model.getBannerId();
        this.featureId = model.getFeatureId();
        this.tagIds = model.getTagIds();
        this.content = model.getContent();
    }
}
