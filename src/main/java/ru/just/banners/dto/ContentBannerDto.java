package ru.just.banners.dto;

import lombok.Data;
import ru.just.banners.model.dao.BannerRecord;

@Data
public class ContentBannerDto {
    private String content;

    public ContentBannerDto(BannerRecord bannerModel) {
       content = bannerModel.getContent();
    }
}
