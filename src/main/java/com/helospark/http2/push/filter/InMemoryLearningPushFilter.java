package com.helospark.http2.push.filter;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helospark.http2.push.filter.helper.ConfigurationVerifier;
import com.helospark.http2.push.filter.helper.FilteringEnabledPredicate;
import com.helospark.http2.push.filter.helper.HttpServletHeaderExtractor;
import com.helospark.http2.push.filter.helper.PushCacheEvictor;
import com.helospark.http2.push.filter.helper.PushCacheSecondaryResourceAppender;
import com.helospark.http2.push.filter.helper.RelativeRefererPathExtractor;
import com.helospark.http2.push.filter.helper.ResourcePushService;
import com.helospark.http2.push.filter.helper.SecondaryResourceAssosiatedWithPrimaryResourcePredicate;
import com.helospark.http2.push.filter.helper.StatefulPushPerformer;

public class InMemoryLearningPushFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryLearningPushFilter.class);
    private FilteringEnabledPredicate filteringEnabledPredicate;
    private StatefulPushPerformer statefulPushPerformer;
    private ConfigurationVerifier configurationVerifier;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug("Instantiating dependencies for push filter");
        configurationVerifier = new ConfigurationVerifier();

        HttpServletHeaderExtractor httpServletHeaderExtractor = new HttpServletHeaderExtractor();
        filteringEnabledPredicate = new FilteringEnabledPredicate(httpServletHeaderExtractor);

        RelativeRefererPathExtractor relativeRefererPathExtractor = new RelativeRefererPathExtractor(httpServletHeaderExtractor);
        Random random = new Random();
        PushCacheEvictor pushCacheEvictor = new PushCacheEvictor(random);
        ResourcePushService resourcePushService = new ResourcePushService();
        PushCacheSecondaryResourceAppender pushCacheSecondaryResourceAppender = new PushCacheSecondaryResourceAppender();
        SecondaryResourceAssosiatedWithPrimaryResourcePredicate secondaryResourceAssosiatedWithPrimaryResourcePredicate = new SecondaryResourceAssosiatedWithPrimaryResourcePredicate();

        statefulPushPerformer = new StatefulPushPerformer(relativeRefererPathExtractor, pushCacheEvictor, resourcePushService,
                pushCacheSecondaryResourceAppender, secondaryResourceAssosiatedWithPrimaryResourcePredicate);
        configurationVerifier = new ConfigurationVerifier();

        LOGGER.debug("Instatiated dependencies, verifying configuration");
        configurationVerifier.assertConfigValid(filterConfig);
        LOGGER.debug("Configuration verified");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                if (filteringEnabledPredicate.test(httpServletRequest)) {
                    statefulPushPerformer.filterInternal(httpServletRequest);
                } else {
                    LOGGER.debug(httpServletRequest.getRequestURI() + " " + httpServletRequest.getQueryString() + " DISABLED");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while performing push filter", e);
        }
        chain.doFilter(request, response);

        removedNonCachedResourcesFromPushCache(request, response);
    }

    private void removedNonCachedResourcesFromPushCache(ServletRequest request, ServletResponse response) {
        if (response instanceof HttpServletResponse && request instanceof HttpServletRequest) {
            HttpServletResponse resp = (HttpServletResponse) response;
            Optional<String> cacheControl = Optional.ofNullable(resp.getHeader("Cache-Control"));
            boolean isUriCached = cacheControl.map(header -> header.contains("public"))
                    .orElse(false);
            HttpServletRequest httpServletRequest = (HttpServletRequest) request; // TODO!
            if (!isUriCached) {
                statefulPushPerformer.removeFromCache(httpServletRequest.getRequestURI());
            }
            LOGGER.debug("HEADER: " + cacheControl);
        }
    }

    @Override
    public void destroy() {

    }

}
