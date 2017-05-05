package com.helospark.http2.push.filter.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helospark.http2.push.filter.helper.domain.PrimaryResourceData;

public class PushCacheEvictor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushCacheEvictor.class);
    private static final Float SECONDARY_RESOURCE_EVICT_CHANGE = 5.0f / 100.0f;
    private final Random random;

    public PushCacheEvictor(Random random) {
        this.random = random;
    }

    public void randomlyEvictSecondaryResourceFromPushCache(Map<String, PrimaryResourceData> pushCache, String requestUri) {
        if (random.nextFloat() <= SECONDARY_RESOURCE_EVICT_CHANGE) {
            Map<String, String> secondaryResources = getSecondaryResources(pushCache, requestUri);
            tryRemovingRandomSecondaryResource(pushCache, requestUri, secondaryResources);
        }
    }

    private Map<String, String> getSecondaryResources(Map<String, PrimaryResourceData> pushCache, String requestUri) {
        PrimaryResourceData primaryResourceData = pushCache.get(requestUri);
        return primaryResourceData.getSecondaryResources();
    }

    /**
     * It is possible  that this key was already removed by other thread in the meantime
     * this could be fixed by making a synchronized block inside the if, but
     * for performance reasons I have avoided it, considering
     * that it is causing no problem since remove returns no error on missing key
     * and removing already controlled by randomness.
     * @param pushCache
     * @param requestUri
     * @param secondaryResources
     */
    private void tryRemovingRandomSecondaryResource(Map<String, PrimaryResourceData> pushCache, String requestUri, Map<String, String> secondaryResources) {
        List<String> valuesList = new ArrayList<>(secondaryResources.keySet());
        String keyToRemove = valuesList.get(random.nextInt(valuesList.size()));
        pushCache.remove(keyToRemove);
        LOGGER.debug("Evicting {} from {}", keyToRemove, requestUri);
    }
}
