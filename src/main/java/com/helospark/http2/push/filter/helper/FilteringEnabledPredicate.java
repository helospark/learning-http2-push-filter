package com.helospark.http2.push.filter.helper;

import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

public class FilteringEnabledPredicate implements Predicate<HttpServletRequest> {
    private final HttpServletHeaderExtractor httpServletHeaderExtractor;

    public FilteringEnabledPredicate(HttpServletHeaderExtractor httpServletHeaderExtractor) {
        this.httpServletHeaderExtractor = httpServletHeaderExtractor;
    }

    @Override
    public boolean test(HttpServletRequest httpServletRequest) {
        Optional<String> userAgent = Optional.ofNullable(httpServletRequest.getHeader("User-Agent"));
        return !isUserAgentBlacklisted(userAgent) && !isNoPushQueryParameterPresent(httpServletRequest);
    }

    private boolean isUserAgentBlacklisted(Optional<String> userAgent) {
        return false;
    }

    private boolean isNoPushQueryParameterPresent(HttpServletRequest httpServletRequest) {
        return httpServletHeaderExtractor.extractHeader(httpServletRequest, "X-nopush")
                .isPresent();
    }

}
