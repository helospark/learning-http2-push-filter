package com.helospark.http2.push.filter.helper.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PrimaryResourceData {
    private static final Integer NUMBER_OF_REQUESTS_TO_PROMOTE = 1;
    private final Map<String, String> secondaryResources = new ConcurrentHashMap<>();
    private final Map<String, Integer> possibleSecondaryResources = new ConcurrentHashMap<>();

    public void addSecondaryResource(String secondaryResourceUri) {
        if (!secondaryResources.containsKey(secondaryResourceUri)) {
            possibleSecondaryResources.putIfAbsent(secondaryResourceUri, 0);
            Optional<Integer> computed = Optional.ofNullable(possibleSecondaryResources.computeIfPresent(secondaryResourceUri, (key, value) -> value + 1));
            boolean shouldPromote = computed
                    .map(numberOfTimesResourceIsRequested -> numberOfTimesResourceIsRequested >= NUMBER_OF_REQUESTS_TO_PROMOTE)
                    .orElse(false);
            if (shouldPromote) {
                possibleSecondaryResources.remove(secondaryResourceUri);
                secondaryResources.putIfAbsent(secondaryResourceUri, secondaryResourceUri); // same key and value for the moment
            }
        }
    }

    public Map<String, String> getSecondaryResources() {
        return secondaryResources;
    }

    public Map<String, Integer> getPossibleSecondaryResources() {
        return possibleSecondaryResources;
    }

    @Override
    public String toString() {
        return "PrimaryResourceData [secondaryResources=" + secondaryResources + ", possibleSecondaryResources=" + possibleSecondaryResources + "]";
    }

}
