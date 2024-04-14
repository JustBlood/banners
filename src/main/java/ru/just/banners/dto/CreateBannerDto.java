package ru.just.banners.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateBannerDto {
    private Long featureId;
    private List<Long> tagIds;
    private String content;
    private Boolean isActive;
}
