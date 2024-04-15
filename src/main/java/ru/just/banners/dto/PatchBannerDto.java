package ru.just.banners.dto;

import lombok.Data;
import ru.just.banners.dto.validation.ValidJson;

import java.util.List;

@Data
public class PatchBannerDto {
    private Long featureId;
    private List<Long> tagIds;
    @ValidJson(message = "должен быть валидным JSON")
    private String content;
    private Boolean isActive;

}
