package com.epam.dlab.billing.gcp.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

@Data
@ToString
public class BillingConfigurationGcp {
    private final Integer initialDelay;
    private final Integer frequency;
    private final TimeUnit timeUnit;

    @Builder
    public BillingConfigurationGcp(Integer initialDelay, Integer frequency, TimeUnit timeUnit) {
        this.initialDelay = initialDelay;
        this.frequency = frequency;
        this.timeUnit = timeUnit;
    }
}
