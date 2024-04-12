package ru.just.banners.model;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class BannerRecord {
    @Column(name = "BANNER_ID")
    private Long bannerId;
    @Column(name = "FEATURE_ID")
    private Long featureId;
    @Column(name = "TAG_ID")
    private Long tagId;
    @Column(name = "CONTENT")
    private String content;
}
