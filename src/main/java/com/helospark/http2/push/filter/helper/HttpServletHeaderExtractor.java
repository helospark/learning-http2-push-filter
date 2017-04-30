package com.helospark.http2.push.filter.helper;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

public class HttpServletHeaderExtractor {

    public Optional<String> extractReferer(HttpServletRequest httpServletRequest) {
        return extractHeader(httpServletRequest, "referer");
    }

    public Optional<String> extractHeader(HttpServletRequest httpServletRequest, String headerName) {
        return Optional.ofNullable(httpServletRequest.getHeader(headerName));
    }
}
