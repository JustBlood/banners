package ru.just.banners.repository;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Query;
import org.springframework.stereotype.Repository;
import ru.just.banners.dto.CreateBannerDto;
import ru.just.banners.model.dao.BannerAuditModel;
import ru.just.banners.model.dao.BannerFullModel;
import ru.just.banners.model.dao.BannerRecord;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;
import static ru.just.banners.Tables.BANNER_AUDIT;
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

    public List<BannerFullModel> findBanners(Optional<Long> featureId, Optional<Long> tagId, Integer offset, Integer limit) {
        final Condition isTagInBannerTagsCondition = tagId
                .map(tag -> inline(tag)
                        .eq(any(arrayAgg(BANNER_FEATURE_TAG.TAG_ID))))
                .orElse(trueCondition());
        return jooq.select(
                        BANNER.BANNER_ID.as("BANNER_ID"),
                        BANNER.FEATURE_ID.as("FEATURE_ID"),
                        arrayAgg(BANNER_FEATURE_TAG.TAG_ID).as("TAGS"),
                        BANNER.CONTENT.as("CONTENT"),
                        BANNER.IS_ACTIVE.as("IS_ACTIVE")
                )
                .from(BANNER_FEATURE_TAG.join(BANNER).using(BANNER.BANNER_ID))
                .groupBy(BANNER.BANNER_ID, BANNER.FEATURE_ID, md5(BANNER.CONTENT.cast(String.class)), BANNER.IS_ACTIVE)
                .having(
                        featureId.map(BANNER.FEATURE_ID::eq).orElse(trueCondition())
                                .and(isTagInBannerTagsCondition))
                .offset(offset)
                .limit(limit)
                .fetchInto(BannerFullModel.class);
    }

    public long createBanner(CreateBannerDto createBannerDto) {
        long bannerId = Objects.requireNonNull(jooq.insertInto(BANNER)
                .set(BANNER.FEATURE_ID, createBannerDto.getFeatureId())
                .set(BANNER.CONTENT, JSON.valueOf((createBannerDto.getContent())))
                .set(BANNER.IS_ACTIVE, true)
                .returning(BANNER.BANNER_ID)
                .fetchOne()).getValue(BANNER.BANNER_ID);

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

    public void patchBanner(BannerFullModel bannerModel) {
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

    public List<BannerAuditModel> findBannerVersions(Long bannerId) {
        return jooq.selectFrom(BANNER_AUDIT)
                .where(BANNER_AUDIT.BANNER_ID.eq(bannerId)
                        .and(BANNER_AUDIT.OPERATION_TYPE.eq(OperationType.UPDATE.name()))
                )
                .orderBy(BANNER_AUDIT.OPERATION_TIME.desc())
                .limit(3)
                .fetchInto(BannerAuditModel.class);
    }

    public BannerFullModel findBannerById(Long bannerId) {
        return jooq.select(
                    BANNER.BANNER_ID,
                    BANNER.FEATURE_ID,
                    arrayAgg(BANNER_FEATURE_TAG.TAG_ID).as("TAGS"),
                    BANNER.CONTENT,
                    BANNER.IS_ACTIVE
                )
                .from(BANNER_FEATURE_TAG.join(BANNER).using(BANNER.BANNER_ID))
                .where(BANNER.BANNER_ID.eq(bannerId))
                .groupBy(BANNER.BANNER_ID, BANNER.FEATURE_ID)
                .fetchOneInto(BannerFullModel.class);
    }

    public BannerAuditModel findBannerVersion(Long versionId) {
        return jooq.selectFrom(BANNER_AUDIT)
                .where(BANNER_AUDIT.BANNER_AUDIT_ID.eq(versionId))
                .fetchOneInto(BannerAuditModel.class);
    }
}
