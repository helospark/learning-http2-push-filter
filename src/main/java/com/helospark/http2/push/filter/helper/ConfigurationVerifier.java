package com.helospark.http2.push.filter.helper;

import javax.servlet.FilterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationVerifier.class);
    private static final boolean FAIL_FAST_ON_SERVLET_VERSION_MISMATCH = false;

    public void assertConfigValid(FilterConfig filterConfig) {
        int majorVersion = filterConfig.getServletContext().getMajorVersion();
        if (majorVersion < 4) {
            if (FAIL_FAST_ON_SERVLET_VERSION_MISMATCH) {
                throw new IllegalStateException("This filter required at minimum Servlet version 4 to use push feature");
            } else {
                LOGGER.error("Servlet 4 is not present");
            }
        }
    }

}
