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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.just.banners.dto.BannerDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = "/insert_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "/drop_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class BannersControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
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
    void findBanner(Long tagId, Long featureId, Long expectedBannerId) {
        String content = mockMvc.perform(get("/api/v1/banner")
                        .param("tag_id", String.valueOf(tagId))
                        .param("feature_id", String.valueOf(featureId))
                        .header("X-Auth-Key", adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<BannerDto> banners = objectMapper.readValue(content, new TypeReference<>() {});
        BannerDto banner = banners.getFirst();
        assertEquals(1, banners.size());
        assertEquals(expectedBannerId, banners.getFirst().getBannerId());
        assertEquals(featureId, banner.getFeatureId());
        assertFalse(banner.getTagIds().isEmpty());
        assertTrue(banner.getTagIds().contains(tagId));
    }
}
