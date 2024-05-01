package ru.just.banners.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.just.banners.dto.validation.ValidJson;

import java.util.List;

@Data
public class CreateBannerDto {
    @NotNull(message = "должно быть указано")
    private Long featureId;
    @NotEmpty(message = "не должно быть пустым")
    private List<Long> tagIds;
    @NotBlank(message = "не должно быть пустым")
    @ValidJson(message = "должно быть валидным JSON")
    private String content;
    private Boolean isActive = true;
}
