package com.helospark.http2.push.filter.helper;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helospark.http2.push.filter.helper.domain.LastUserResource;

public class SecondaryResourceAssosiatedWithPrimaryResourcePredicate {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecondaryResourceAssosiatedWithPrimaryResourcePredicate.class);
    private static final Integer ASSOSIATION_TIME = 4000;

    public boolean test(Map<String, LastUserResource> lastPrimaryResourcePerUser, Optional<String> refererHeader, String userId) {
        LOGGER.debug("## {}, currentTime={}", lastPrimaryResourcePerUser, System.currentTimeMillis());
        if (refererHeader.isPresent()) {
            Optional<LastUserResource> lastResource = Optional.ofNullable(lastPrimaryResourcePerUser.get(userId));
            return lastResource
                    .filter(resource -> timeSince(resource.getTimestampInMilliseconds()) < ASSOSIATION_TIME)
                    .isPresent();
        }
        return false;
    }

    private long timeSince(long lastTime) {
        return System.currentTimeMillis() - lastTime;
    }
}
