package com.helospark.http2.push.filter;

import java.io.IOException;
import java.util.Optional;

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
import com.helospark.http2.push.filter.helper.StatefulPushPerformer;

public class InMemoryLearningPushFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryLearningPushFilter.class);
    private final FilteringEnabledPredicate filteringEnabledPredicate = new FilteringEnabledPredicate();
    private final StatefulPushPerformer statefulPushPerformer = new StatefulPushPerformer();
    private final ConfigurationVerifier configurationVerifier = new ConfigurationVerifier();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        configurationVerifier.assertConfigValid(filterConfig);

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
