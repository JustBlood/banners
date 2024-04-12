package ru.just.banners.repository;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.just.banners.dto.BannerDto;
import ru.just.banners.dto.BannerIdDto;
import ru.just.banners.dto.CreateBannerDto;
import ru.just.banners.model.BannerRecord;

import java.util.Optional;

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

    public BannerDto findBanners(Optional<Long> featureId, Optional<Long> tagId, Integer offset, Integer limit) {
        throw new NotImplementedException();
    }

    public BannerIdDto createBanner(CreateBannerDto createBannerDto) {
        throw new NotImplementedException();
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
