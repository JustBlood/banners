package ru.just.banners.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.just.banners.model.dao.BannerFullModel;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class BannerDto {
    private Long bannerId;
    private Long featureId;
    private List<Long> tagIds;
    private String content;
    private Boolean isActive;

    public BannerDto(BannerFullModel model) {
        this.bannerId = model.getBannerId();
        this.featureId = model.getFeatureId();
        this.tagIds = model.getTagIds();
        this.content = model.getContent();
        this.isActive = model.getIsActive();
    }
}
