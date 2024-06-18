package ru.just.banners.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.just.banners.repository.BannersRepository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class DelayDeletingService {
    private final BannersRepository bannersRepository;
    @Value("${banners.batch.delete}")
    private Integer countForDeleteInBatch;

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    @Transactional
    void deleteBannersByFlag() {
        bannersRepository.deleteNBannersByFlag(countForDeleteInBatch);
    }
}
