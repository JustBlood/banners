package ru.just.banners.model.dao;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class BannerFeatureTagRecord {
        @Column(name = "BANNER_ID")
        private Long bannerId;
        @Column(name = "FEATURE_ID")
        private Long featureId;
        @Column(name = "TAG_ID")
        private Long tagId;
}
