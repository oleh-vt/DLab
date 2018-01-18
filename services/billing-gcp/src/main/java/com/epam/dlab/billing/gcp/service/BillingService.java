package com.epam.dlab.billing.gcp.service;

public interface BillingService extends Runnable {

    void loadReport();

    @Override
    default void run() {
        loadReport();
    }
}
