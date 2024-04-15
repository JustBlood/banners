package ru.just.banners.repository;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Query;
import org.springframework.stereotype.Repository;
import ru.just.banners.dto.CreateBannerDto;
import ru.just.banners.model.dao.BannerRecord;
import ru.just.banners.model.domain.BannerModel;

import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.trueCondition;
import static ru.just.banners.tables.Banner.BANNER;
import static ru.just.banners.tables.BannerFeatureTag.BANNER_FEATURE_TAG;


@RequiredArgsConstructor
@Repository
// FIXME: обработка ошибок
public class BannersRepository {
    private final DSLContext jooq;

    public @Nullable BannerRecord findBannerByFeatureAndTag(Long featureId, Long tagId, Boolean useLastRevision) {
        return jooq.select()
                .from(BANNER_FEATURE_TAG.join(BANNER).using(BANNER.BANNER_ID))
                .where(BANNER_FEATURE_TAG.FEATURE_ID.eq(featureId))
                .and(BANNER_FEATURE_TAG.TAG_ID.eq(tagId))
                .and(BANNER.IS_ACTIVE.isTrue())
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
                bannerModel.setIsActive(record.getIsActive());
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

    public void patchBanner(BannerModel bannerModel) {
        var query = jooq.updateQuery(BANNER);
        query.addConditions(BANNER.BANNER_ID.eq(bannerModel.getBannerId()));
        if (Objects.nonNull(bannerModel.getFeatureId()))
            query.addValue(BANNER.FEATURE_ID, bannerModel.getFeatureId());
        if (Objects.nonNull(bannerModel.getContent()))
            query.addValue(BANNER.CONTENT, JSON.valueOf(bannerModel.getContent()));
        if (Objects.nonNull(bannerModel.getIsActive()))
            query.addValue(BANNER.IS_ACTIVE, bannerModel.getIsActive());
        if (query.execute() < 1) {
            throw new EntityNotFoundException("Баннер не найден");
        }

        jooq.deleteFrom(BANNER_FEATURE_TAG)
                .where(BANNER_FEATURE_TAG.BANNER_ID.eq(bannerModel.getBannerId())
                .and(BANNER_FEATURE_TAG.TAG_ID.notIn(bannerModel.getTagIds())))
                .execute();

        List<Query> insertQueries = bannerModel.getTagIds().stream()
                .map(tagId ->
                        jooq.insertInto(BANNER_FEATURE_TAG)
                                .set(BANNER_FEATURE_TAG.BANNER_ID, bannerModel.getBannerId())
                                .set(BANNER_FEATURE_TAG.FEATURE_ID, bannerModel.getFeatureId())
                                .set(BANNER_FEATURE_TAG.TAG_ID, tagId)
                                .onDuplicateKeyIgnore()
                )
                .collect(Collectors.toList());

        jooq.batch(insertQueries).execute();
    }

    public void deleteBannerById(Long bannerId) {
         if (jooq.delete(BANNER).where(BANNER.BANNER_ID.eq(bannerId)).execute() < 1) {
             throw new EntityNotFoundException("Баннер не найден");
         }
    }
}
