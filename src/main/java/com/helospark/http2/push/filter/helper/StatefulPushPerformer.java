package com.helospark.http2.push.filter.helper;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helospark.http2.push.filter.helper.domain.LastUserResource;
import com.helospark.http2.push.filter.helper.domain.PrimaryResourceData;

public class StatefulPushPerformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatefulPushPerformer.class);

    private final Map<String, LastUserResource> lastPrimaryResourcePerUser = new ConcurrentHashMap<>();
    private final Map<String, PrimaryResourceData> pushCacheMap = new ConcurrentHashMap<>();

    private final RelativeRefererPathExtractor relativeRefererPathExtractor;
    private final PushCacheEvictor pushCacheEvictor;
    private final ResourcePushService resourcePushService;
    private final PushCacheSecondaryResourceAppender pushCacheSecondaryResourceAppender;
    private final SecondaryResourceAssosiatedWithPrimaryResourcePredicate secondaryResourceAssosiatedWithPrimaryResourcePredicate;

    public StatefulPushPerformer(RelativeRefererPathExtractor relativeRefererPathExtractor,
            PushCacheEvictor pushCacheEvictor, ResourcePushService resourcePushService, PushCacheSecondaryResourceAppender pushCacheSecondaryResourceAppender,
            SecondaryResourceAssosiatedWithPrimaryResourcePredicate secondaryResourceAssosiatedWithPrimaryResourcePredicate) {
        this.relativeRefererPathExtractor = relativeRefererPathExtractor;
        this.pushCacheEvictor = pushCacheEvictor;
        this.resourcePushService = resourcePushService;
        this.pushCacheSecondaryResourceAppender = pushCacheSecondaryResourceAppender;
        this.secondaryResourceAssosiatedWithPrimaryResourcePredicate = secondaryResourceAssosiatedWithPrimaryResourcePredicate;
    }

    public void filterInternal(HttpServletRequest httpServletRequest) {
        String requestUri = httpServletRequest.getRequestURI();
        String userIp = httpServletRequest.getRemoteAddr();
        Optional<String> referedUrl = relativeRefererPathExtractor.referedResource(httpServletRequest);
        Optional<PrimaryResourceData> primaryResourceData = Optional.ofNullable(pushCacheMap.get(requestUri));

        LOGGER.debug("Processing filter for {}", requestUri);

        if (primaryResourceData.isPresent()) {
            resourcePushService.pushSecondaryResources(httpServletRequest, primaryResourceData.get(), pushCacheMap);
            pushCacheEvictor.randomlyEvictSecondaryResourceFromPushCache(pushCacheMap, requestUri);
        } else if (secondaryResourceAssosiatedWithPrimaryResourcePredicate.test(lastPrimaryResourcePerUser, referedUrl, userIp)) {
            pushCacheSecondaryResourceAppender.addSecondaryResource(pushCacheMap, httpServletRequest, referedUrl.get());
        } else {
            LOGGER.debug("Ignoring resource pushMap='{}', requestUri='{}', referer='{}'", new Object[] { pushCacheMap, requestUri, referedUrl });
        }
        lastPrimaryResourcePerUser.put(userIp, new LastUserResource(System.currentTimeMillis(), requestUri));
    }

    public void removeFromCache(String uri) {
        pushCacheMap.remove(uri);
    }
}
