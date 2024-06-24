package ru.just.banners.e2e;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import ru.just.banners.dto.*;
import ru.just.banners.schedule.DelayDeletingService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = "/insert_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/drop_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BannerAdminTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DelayDeletingService delayDeletingService;
    final String adminToken = "admin-token";

    @Test
    @SneakyThrows
    void createBannerThenUpdateAndRollback_whenCreateNewBanner_bannerWithRollbackedContent() {
        long featureId = 11L;
        List<Long> tagIds = List.of(99L, 98L, 97L);
        String content = "{\"message\": \"Banner 11\"}";
        CreateBannerDto createBannerDto = new CreateBannerDto();
        createBannerDto.setFeatureId(featureId);
        createBannerDto.setTagIds(tagIds);
        createBannerDto.setContent(content);
        PatchBannerDto patchBannerDto = new PatchBannerDto();
        final String updatedContent = "{}";
        patchBannerDto.setContent(updatedContent);
        patchBannerDto.setFeatureId(featureId);
        patchBannerDto.setTagIds(tagIds);
        patchBannerDto.setIsActive(false);
        LinkedMultiValueMap<String, String> featureIdRequestParam = new LinkedMultiValueMap<>();
        featureIdRequestParam.put("feature_id", List.of(String.valueOf(featureId)));

        String response = mockMvc.perform(post("/api/v1/banner")
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header("X-Auth-Key", adminToken)
                        .content(objectMapper.writeValueAsString(createBannerDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        BannerIdDto banner = objectMapper.readValue(response, BannerIdDto.class);
        final Long bannerId = banner.getBannerId();
        mockMvc.perform(patch("/api/v1/banner/" + bannerId)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header("X-Auth-Key", adminToken)
                        .content(objectMapper.writeValueAsString(patchBannerDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        featureIdRequestParam.put("feature_id", List.of("1"));
        mockMvc.perform(delete("/api/v1/banner")
                        .params(featureIdRequestParam)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        delayDeletingService.deleteBannersByFlag();
        String bannersAfterCreateAndUpdateResponse = mockMvc.perform(get("/api/v1/banner")
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<BannerDto> bannersAfterCreateAndUpdate = objectMapper.readValue(bannersAfterCreateAndUpdateResponse,
                new TypeReference<>() {
                });
        String bannerAuditDtosResponse = mockMvc.perform(
                        get("/api/v1/banner/" + bannerId + "/versions")
                                .header("X-Auth-Key", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<BannerAuditDto> bannerAuditDtos = objectMapper.readValue(bannerAuditDtosResponse, new TypeReference<>() {
        });
        final long versionId = bannerAuditDtos.getFirst().getVersionId();
        String rollbackUrl = "/api/v1/banner/%s/versions/%s/rollback";
        String rollbackBannerResponse = mockMvc.perform(put(String.format(rollbackUrl, bannerId, versionId))
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        BannerDto rollbackedBanner = objectMapper.readValue(rollbackBannerResponse, BannerDto.class);
        final BannerDto bannerDtoAfterUpdate = bannersAfterCreateAndUpdate.stream().filter(b -> b.getBannerId().equals(bannerId)).findFirst().orElseThrow();

        assertEquals(featureId, bannerDtoAfterUpdate.getFeatureId());
        assertEquals(updatedContent, bannerDtoAfterUpdate.getContent());
        assertEquals(featureId, rollbackedBanner.getFeatureId());
        assertEquals(tagIds, rollbackedBanner.getTagIds());
        assertEquals(content, rollbackedBanner.getContent());
    }
}
