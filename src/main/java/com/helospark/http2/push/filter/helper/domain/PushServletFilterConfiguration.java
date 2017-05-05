package com.helospark.http2.push.filter.helper.domain;

import javax.annotation.Generated;

public class PushServletFilterConfiguration {
    private final boolean failFastOnOldServlet;
    private final double secondaryResourceEvictionChance;
    private final int secondaryResourceAssitiationTime;

    @Generated("SparkTools")
    private PushServletFilterConfiguration(Builder builder) {
        this.failFastOnOldServlet = builder.failFastOnOldServlet;
        this.secondaryResourceEvictionChance = builder.secondaryResourceEvictionChance;
        this.secondaryResourceAssitiationTime = builder.secondaryResourceAssitiationTime;
    }

    /**
     * Creates builder to build {@link PushServletFilterConfiguration}.
     * @return created builder
     */
    @Generated("SparkTools")
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to build {@link PushServletFilterConfiguration}.
     */
    @Generated("SparkTools")
    public static final class Builder {
        private boolean failFastOnOldServlet;
        private double secondaryResourceEvictionChance;
        private int secondaryResourceAssitiationTime;

        private Builder() {
        }

        public Builder withFailFastOnOldServlet(boolean failFastOnOldServlet) {
            this.failFastOnOldServlet = failFastOnOldServlet;
            return this;
        }

        public Builder withSecondaryResourceEvictionChance(double secondaryResourceEvictionChance) {
            this.secondaryResourceEvictionChance = secondaryResourceEvictionChance;
            return this;
        }

        public Builder withSecondaryResourceAssitiationTime(int secondaryResourceAssitiationTime) {
            this.secondaryResourceAssitiationTime = secondaryResourceAssitiationTime;
            return this;
        }

        public PushServletFilterConfiguration build() {
            return new PushServletFilterConfiguration(this);
        }
    }

}
