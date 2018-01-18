package com.epam.dlab.billing.gcp.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BillingServiceGcp implements BillingService {
    @Override
    public void loadReport() {
        log.info("Loading report ...");
        try {
            Thread.sleep(5000L);
            log.info("Loading report finished!");
        } catch (InterruptedException e) {
            log.error("Interrupted");
            Thread.currentThread().interrupt();
        }
    }
}
