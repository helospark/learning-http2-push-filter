package com.helospark.http2.push.filter.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.PushBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helospark.http2.push.filter.helper.domain.PrimaryResourceData;

public class ResourcePushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcePushService.class);
    private final Map<String, Object> pushLogMap = new ConcurrentHashMap<String, Object>();

    public void pushSecondaryResources(HttpServletRequest httpServletRequest, PrimaryResourceData primaryResourceData, Map<String, PrimaryResourceData> pushCacheMap) {
        Stream<String> resourceStreamToPush = collectResourcesToPush(primaryResourceData, pushCacheMap);
        pushResources(httpServletRequest, resourceStreamToPush);
    }

    private Stream<String> collectResourcesToPush(PrimaryResourceData primaryResourceData, Map<String, PrimaryResourceData> pushCacheMap) {
        ArrayList<String> topLevelResources = new ArrayList<>(primaryResourceData.getSecondaryResources().values());
        Stream<String> singleDepthTransitiveResources = getTransitiveResourceStream(pushCacheMap, topLevelResources);
        return Stream.concat(singleDepthTransitiveResources, topLevelResources.stream());
    }

    private void pushResources(HttpServletRequest httpServletRequest, Stream<String> resourceStreamToPush) {
        PushBuilder pushBuilder = createPushBuilderFor(httpServletRequest);
        String userAddress = httpServletRequest.getRemoteAddr();
        Object lock = getLockForIp(userAddress);
        // Only a single thread may push to a single user, otherwise the streamIds will
        // not be ordered and browser throws error on push
        synchronized (lock) {
            resourceStreamToPush
                    .distinct()
                    .forEach(path -> pushResource(pushBuilder, path));
        }
        pushLogMap.remove(userAddress);
    }

    private Stream<String> getTransitiveResourceStream(Map<String, PrimaryResourceData> pushCacheMap, List<String> resourcesToPush) {
        return resourcesToPush.stream()
                .flatMap(path -> getTransitiveResources(pushCacheMap, path));
    }

    private PushBuilder createPushBuilderFor(HttpServletRequest httpServletRequest) {
        PushBuilder pushBuilder = httpServletRequest.newPushBuilder();
        pushBuilder.addHeader("X-noPush", "true");
        return pushBuilder;
    }

    private Object getLockForIp(String userAddress) {
        Object newLock = new Object();
        return Optional.ofNullable(pushLogMap.putIfAbsent(userAddress, newLock))
                .orElse(newLock);
    }

    private Stream<String> getTransitiveResources(Map<String, PrimaryResourceData> pushCacheMap, String path) {
        return Optional.ofNullable(pushCacheMap.get(path))
                .map(primaryResource -> primaryResource.getSecondaryResources().values())
                .orElse(Collections.emptyList())
                .stream();
    }

    private void pushResource(PushBuilder pushBuilder, String path) {
        LOGGER.debug("Pushing {}", path);
        pushBuilder
                .path(path)
                .push();
    }
}
