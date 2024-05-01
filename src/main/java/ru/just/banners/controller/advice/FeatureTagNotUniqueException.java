package ru.just.banners.controller.advice;

import lombok.Getter;

import java.util.List;

@Getter
public class FeatureTagNotUniqueException extends RuntimeException {
    private final Long featureId;
    private final List<String> tagIds;

    public FeatureTagNotUniqueException(Long featureId, List<Long> tagIds) {
        this.featureId = featureId;
        this.tagIds = tagIds.stream().map(Object::toString).toList();
    }
}
