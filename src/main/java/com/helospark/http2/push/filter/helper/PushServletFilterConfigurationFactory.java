package com.helospark.http2.push.filter.helper;

import java.util.Optional;

import javax.servlet.FilterConfig;

import com.helospark.http2.push.filter.helper.domain.PushServletFilterConfiguration;

public class PushServletFilterConfigurationFactory {

    public PushServletFilterConfiguration createConfiguration(FilterConfig filterConfig) {
        return PushServletFilterConfiguration.builder()
                .withFailFastOnOldServlet(initParameterToBoolean(filterConfig, "failFastOnOldServlet", true))
                .withSecondaryResourceEvictionChance(initParameterToDouble(filterConfig, "secondaryResourceEvictionChance", 0.05))
                .withSecondaryResourceAssitiationTime(initParameterToInt(filterConfig, "secondaryResourceAssotiationTime", 4000))
                .build();
    }

    private boolean initParameterToBoolean(FilterConfig filterConfig, String name, boolean defaultValue) {
        return Optional.ofNullable(filterConfig.getInitParameter("failFastOnOldServlet"))
                .map(value -> Boolean.valueOf(value))
                .orElse(defaultValue);
    }

    private double initParameterToDouble(FilterConfig filterConfig, String name, double defaultValue) {
        return Optional.ofNullable(filterConfig.getInitParameter("failFastOnOldServlet"))
                .map(value -> Double.valueOf(value))
                .orElse(defaultValue);
    }

    private int initParameterToInt(FilterConfig filterConfig, String name, int defaultValue) {
        return Optional.ofNullable(filterConfig.getInitParameter("failFastOnOldServlet"))
                .map(value -> Integer.valueOf(value))
                .orElse(defaultValue);
    }
}
