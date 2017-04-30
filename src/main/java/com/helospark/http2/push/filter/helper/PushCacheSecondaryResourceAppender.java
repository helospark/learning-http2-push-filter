package com.helospark.http2.push.filter.helper;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helospark.http2.push.filter.helper.domain.PrimaryResourceData;

public class PushCacheSecondaryResourceAppender {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushCacheSecondaryResourceAppender.class);

    public void addSecondaryResource(Map<String, PrimaryResourceData> pushCacheMap, HttpServletRequest httpServletRequest, String refererHeaderValue) {
        PrimaryResourceData newPushResource = new PrimaryResourceData();
        PrimaryResourceData primaryResourceToUpdate = Optional.ofNullable(pushCacheMap.putIfAbsent(refererHeaderValue, newPushResource))
                .orElse(newPushResource);
        String requestUri = httpServletRequest.getRequestURI();
        primaryResourceToUpdate.addSecondaryResource(requestUri);
        LOGGER.debug("Added resource {} to baseUri {}", requestUri, refererHeaderValue);
    }

}
