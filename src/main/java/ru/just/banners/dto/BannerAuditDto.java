package ru.just.banners.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.just.banners.model.dao.BannerAuditModel;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerAuditDto {
    private Long versionId;
    private Long bannerId;
    private Long featureId;
    private List<Long> tagIds;
    private String content;
    private Boolean isActive;

    public BannerAuditDto(BannerAuditModel model) {
        this.versionId = model.getBannerAuditId();
        this.bannerId = model.getBannerId();
        this.featureId = model.getFeatureId();
        this.tagIds = model.getTagIds();
        this.content = model.getContent();
        this.isActive = model.getIsActive();
    }
}
