package com.helospark.http2.push.filter.helper.domain;

public class LastUserResource {
    private final long timestampInMilliseconds;
    private final String primaryResourceUri;

    public LastUserResource(long timestampInMilliseconds, String primaryResourceUri) {
        this.timestampInMilliseconds = timestampInMilliseconds;
        this.primaryResourceUri = primaryResourceUri;
    }

    public long getTimestampInMilliseconds() {
        return timestampInMilliseconds;
    }

    public String getPrimaryResourceUri() {
        return primaryResourceUri;
    }

    @Override
    public String toString() {
        return "LastUserResource [timestampInMilliseconds=" + timestampInMilliseconds + ", primaryResourceUri=" + primaryResourceUri + "]";
    }

}
