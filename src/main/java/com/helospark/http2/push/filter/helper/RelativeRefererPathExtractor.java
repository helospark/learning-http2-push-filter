package com.helospark.http2.push.filter.helper;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

public class RelativeRefererPathExtractor {
    private final HttpServletHeaderExtractor httpServletHeaderExtractor;

    public RelativeRefererPathExtractor(HttpServletHeaderExtractor httpServletHeaderExtractor) {
        this.httpServletHeaderExtractor = httpServletHeaderExtractor;
    }

    public Optional<String> referedResource(HttpServletRequest httpServletRequest) {
        return httpServletHeaderExtractor.extractReferer(httpServletRequest)
                .map(referer -> getRelativeUrlFromReferer(referer));

    }

    private String getRelativeUrlFromReferer(String refererHeaderValue) {
        // TODO: This is a quick solution, make properly
        String withoutProtocol = refererHeaderValue.replaceFirst("://", "");
        return withoutProtocol.substring(withoutProtocol.indexOf("/"));
    }

}
