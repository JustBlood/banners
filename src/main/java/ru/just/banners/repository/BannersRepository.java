package ru.just.banners.repository;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.just.banners.dto.BannerIdDto;
import ru.just.banners.dto.CreateBannerDto;
import ru.just.banners.model.dao.BannerFeatureTagRecord;
import ru.just.banners.model.domain.BannerModel;
import ru.just.banners.model.dao.BannerRecord;

import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.trueCondition;
import static ru.just.banners.tables.Banner.BANNER;
import static ru.just.banners.tables.BannerFeatureTag.BANNER_FEATURE_TAG;


@RequiredArgsConstructor
@Repository
public class BannersRepository {
    private final DSLContext jooq;

    public @Nullable BannerRecord findBannerByFeatureAndTag(Long featureId, Long tagId, Boolean useLastRevision) {
        return jooq.select()
                .from(BANNER_FEATURE_TAG.join(BANNER).using(BANNER.BANNER_ID))
                .where(BANNER_FEATURE_TAG.FEATURE_ID.eq(featureId).and(BANNER_FEATURE_TAG.TAG_ID.eq(tagId)))
                .fetchOneInto(BannerRecord.class);
    }

    public List<BannerModel> findBanners(Optional<Long> featureId, Optional<Long> tagId, Integer offset, Integer limit) {
        List<BannerRecord> bannerRecords = jooq
                .selectFrom(BANNER_FEATURE_TAG.join(BANNER).using(BANNER.BANNER_ID))
                .where(featureId.map(BANNER_FEATURE_TAG.FEATURE_ID::eq).orElse(trueCondition()))
                .and(tagId.map(BANNER_FEATURE_TAG.TAG_ID::eq).orElse(trueCondition()))
                .offset(offset)
                .limit(limit)
                .fetchInto(BannerRecord.class);

        Map<Long, BannerModel> bannerModels = new HashMap<>();
        for (var record : bannerRecords) {
            BannerModel model = bannerModels.computeIfAbsent(record.getBannerId(), id -> {
                BannerModel bannerModel = new BannerModel();
                bannerModel.setBannerId(id);
                bannerModel.setFeatureId(record.getFeatureId());
                bannerModel.setContent(record.getContent());
                bannerModel.setTagIds(new ArrayList<>());
                return bannerModel;
            });
            model.getTagIds().add(record.getTagId());
        }

        return new ArrayList<>(bannerModels.values());
    }

    public long createBanner(CreateBannerDto createBannerDto) {
        long bannerId = jooq.insertInto(BANNER)
                .set(BANNER.FEATURE_ID, createBannerDto.getFeatureId())
                .set(BANNER.CONTENT, JSON.valueOf((createBannerDto.getContent())))
                .set(BANNER.IS_ACTIVE, true)
                .returning(BANNER.BANNER_ID)
                .fetchOne().getValue(BANNER.BANNER_ID);

        List<Query> insertQueries = createBannerDto.getTagIds().stream()
                .map(tagId ->
                        jooq.insertInto(BANNER_FEATURE_TAG)
                                .set(BANNER_FEATURE_TAG.BANNER_ID, bannerId)
                                .set(BANNER_FEATURE_TAG.FEATURE_ID, createBannerDto.getFeatureId())
                                .set(BANNER_FEATURE_TAG.TAG_ID, tagId)
                )
                .collect(Collectors.toList());

        jooq.batch(insertQueries).execute();
        return bannerId;
    }

    public BannerIdDto patchBanner(Long bannerId, CreateBannerDto patchBannerDto) {
        throw new NotImplementedException();
    }

    public void deleteBannerById(Long bannerId) {
        // todo: on delete in changelog
        // jooq.delete(BANNER).where(BANNER.BANNER_ID.eq(bannerId)).execute();
        throw new NotImplementedException();
    }
}
