package com.helospark.http2.push.filter.helper.domain;

import java.util.ArrayList;
import java.util.List;

public class SecondaryResource {
    private final List<String> secondaryResourceUriList = new ArrayList<>();

    public List<String> getSecondaryResourceUriList() {
        return secondaryResourceUriList;
    }

    @Override
    public String toString() {
        return "SecondaryResource [secondaryResourceUriList=" + secondaryResourceUriList + "]";
    }

}