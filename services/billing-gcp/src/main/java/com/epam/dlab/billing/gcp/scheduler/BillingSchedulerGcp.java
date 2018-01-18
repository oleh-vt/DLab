package com.epam.dlab.billing.gcp.scheduler;

import com.epam.dlab.billing.gcp.model.BillingConfigurationGcp;
import com.epam.dlab.billing.gcp.service.BillingService;
import com.epam.dlab.billing.gcp.service.BillingServiceGcp;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BillingSchedulerGcp implements Scheduler {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final BillingConfigurationGcp configuration;
    private final BillingService billingService;

    public BillingSchedulerGcp(BillingConfigurationGcp configuration, BillingService billingService) {
        this.configuration = configuration;
        this.billingService = billingService;
    }

    public BillingSchedulerGcp(BillingConfigurationGcp configuration) {
        this(configuration, new BillingServiceGcp());
    }

    @Override
    public void start() {
        log.info("Starting gcp billing scheduler ...");
        executorService.scheduleAtFixedRate(billingService, configuration.getInitialDelay(), configuration.getFrequency(), configuration.getTimeUnit());

    }

    @Override
    public void stop() {
        try {
            log.info("Shutting down gcp billing scheduler");
            executorService.shutdown();

            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                log.warn("Force shutdown of gcp billing scheduler");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("InterruptedException occurred during shutdown: " + e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
