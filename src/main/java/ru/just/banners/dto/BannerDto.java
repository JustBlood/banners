package ru.just.banners.dto;

import lombok.Data;
import ru.just.banners.model.BannerRecord;

@Data
public class BannerDto {
    private String content;

    public BannerDto(BannerRecord bannerModel) {
       content = bannerModel.getContent();
    }
}
