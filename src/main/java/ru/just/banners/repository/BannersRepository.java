package ru.just.banners.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.just.banners.model.BannerRecord;

import static ru.just.banners.tables.Banner.BANNER;


@RequiredArgsConstructor
@Repository
public class BannersRepository {
    private final DSLContext jooq;

    public BannerRecord findBannerByFeatureAndTag(Long featureId, Long tagId, Boolean useLastRevision) {
        return jooq.select()
                .from(BANNER)
                .where(BANNER.FEATURE_ID.eq(featureId).and(BANNER.TAG_ID.eq(tagId)))
                .fetchOne().into(BannerRecord.class);
    }
}
