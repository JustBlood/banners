package ru.just.banners.model.dao;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;

@Data
public class BannerAuditModel {
    @Column(name = "BANNER_AUDIT_ID")
    private Long bannerAuditId;
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

    public BannerAuditModel() {
    }
}
