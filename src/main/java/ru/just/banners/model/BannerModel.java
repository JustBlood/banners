package ru.just.banners.model;

import jakarta.persistence.Column;
import lombok.Data;

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
}
