package ru.just.banners.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import ru.just.banners.controller.advice.ErrorMessage;
import ru.just.banners.dto.BannerDto;
import ru.just.banners.dto.BannerIdDto;
import ru.just.banners.dto.CreateBannerDto;
import ru.just.banners.dto.PatchBannerDto;
import ru.just.banners.schedule.DelayDeletingService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = "/insert_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/drop_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BannersControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private DelayDeletingService delayDeletingService;
    final String adminToken = "admin-token";

    @SneakyThrows
    @Test
    void findUserBanner() {
        mockMvc.perform(get("/api/v1/user_banner?tag_id=1&feature_id=1&use_last_revision=true")
                        .header("X-Auth-Key", "token1"))
                .andExpect(jsonPath("$.message").value("Banner 1"));
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource(
            {
                    "8,4,4",
                    "1,1,1",
                    "7,9,9"
            }
    )
    void findBanner_whenBannerExists_requiredBanner(
            Long tagId, Long featureId, Long expectedBannerId) {
        String content = mockMvc.perform(get("/api/v1/banner")
                        .param("tag_id", String.valueOf(tagId))
                        .param("feature_id", String.valueOf(featureId))
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<BannerDto> banners = objectMapper.readValue(content, new TypeReference<>() {
        });
        BannerDto banner = banners.getFirst();

        assertEquals(1, banners.size());
        assertEquals(expectedBannerId, banners.getFirst().getBannerId());
        assertEquals(featureId, banner.getFeatureId());
        assertFalse(banner.getTagIds().isEmpty());
        assertTrue(banner.getTagIds().contains(tagId));
    }

    @SneakyThrows
    @Test
    void createBannner_whenTagAndFeatureDoesntExists_statusCreated() {
        long featureId = 11L;
        List<Long> tagIds = List.of(99L, 98L, 97L);
        String content = "{\"message\": \"Banner 11\"}";
        CreateBannerDto createBannerDto = new CreateBannerDto();
        createBannerDto.setFeatureId(featureId);
        createBannerDto.setTagIds(tagIds);
        createBannerDto.setContent(content);

        String response = mockMvc.perform(post("/api/v1/banner")
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header("X-Auth-Key", adminToken)
                        .content(objectMapper.writeValueAsString(createBannerDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        BannerIdDto banner = objectMapper.readValue(response, BannerIdDto.class);

        assertNotNull(banner.getBannerId());
    }

    @SneakyThrows
    @Test
    void createBannner_whenTagAndFeatureExists_statusConflict() {
        long featureId = 1L;
        List<Long> tagIds = List.of(1L);
        String allTagIds = tagIds.stream().map(Object::toString).collect(Collectors.joining(","));
        String expectedMessage = messageSource.getMessage(
                "error.featureTagPairExists", new Object[]{allTagIds, featureId}, Locale.of("ru_RU"));
        String content = "{\"message\": \"Banner 1\"}";
        CreateBannerDto createBannerDto = new CreateBannerDto();
        createBannerDto.setFeatureId(featureId);
        createBannerDto.setTagIds(tagIds);
        createBannerDto.setContent(content);

        String response = mockMvc.perform(post("/api/v1/banner")
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header("X-Auth-Key", adminToken)
                        .content(objectMapper.writeValueAsString(createBannerDto)))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertNotNull(response);
        ErrorMessage errorMessage = objectMapper.readValue(response, ErrorMessage.class);
        assertNotNull(errorMessage.getError());
        assertEquals(expectedMessage, errorMessage.getError());
    }

    @SneakyThrows
    @Test
    void deleteBanner_whenNoBannerExists_statusNotFound() {
        long bannerId = 999;
        String expectedMessage = messageSource.getMessage(
                "error.bannerNotFound", null, Locale.of("ru_RU"));

        String response = mockMvc.perform(delete("/api/v1/banner/" + bannerId)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertNotNull(response);
        ErrorMessage errorMessage = objectMapper.readValue(response, ErrorMessage.class);
        assertNotNull(errorMessage.getError());
        assertEquals(expectedMessage, errorMessage.getError());
    }

    @SneakyThrows
    @Test
    void deleteBanner_whenBannerExists_statusNoContent() {
        long bannerId = 1;

        String response = mockMvc.perform(delete("/api/v1/banner/" + bannerId)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertNotNull(response);
        assertEquals("", response);
    }

    @SneakyThrows
    @Test
    void patchBanner_whenBannerExists_statusOk() {
        long bannerId = 1;
        final long featureId = 99L;
        final List<Long> tagIds = List.of(99L, 100L, 101L);
        PatchBannerDto patchBannerDto = new PatchBannerDto();
        patchBannerDto.setContent("{}");
        patchBannerDto.setFeatureId(featureId);
        patchBannerDto.setTagIds(tagIds);
        patchBannerDto.setIsActive(false);

        String response = mockMvc.perform(patch("/api/v1/banner/" + bannerId)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header("X-Auth-Key", adminToken)
                        .content(objectMapper.writeValueAsString(patchBannerDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertNotNull(response);
        assertEquals("", response);
        String getContent = mockMvc.perform(get("/api/v1/banner")
                        .param("tag_id", String.valueOf(tagIds.getFirst()))
                        .param("feature_id", String.valueOf(featureId))
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<BannerDto> banner = objectMapper.readValue(getContent, new TypeReference<>() {});
        assertEquals(featureId, banner.getFirst().getFeatureId());
        assertEquals(tagIds, banner.getFirst().getTagIds());
        assertEquals(bannerId, banner.getFirst().getBannerId());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({
            "1,",
            ",1"
    })
    void deleteBannerByFeatureOrTag_whenBannerExists_statusNoContent(Long featureId, Long tagId) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (featureId != null) params.put("feature_id", List.of(featureId.toString()));
        if (tagId != null) params.put("tag_id", List.of(tagId.toString()));
        mockMvc.perform(delete("/api/v1/banner")
                        .params(params)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        delayDeletingService.deleteBannersByFlag();
        String getContent = mockMvc.perform(get("/api/v1/banner")
                        .params(params)
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<BannerDto> banners = objectMapper.readValue(getContent, new TypeReference<>() {});
        assertTrue(banners.isEmpty());
    }
}
