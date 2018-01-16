/*
 * Copyright (c) 2017, EPAM SYSTEMS INC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.dlab.billing.gcp;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudbilling.Cloudbilling;
import com.google.api.services.cloudbilling.model.BillingAccount;
import com.google.api.services.cloudbilling.model.ListBillingAccountsResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;

@Slf4j
public class BillingApplicationGcp {

    public static void main(String[] args) throws Exception {
        System.out.println("=========Billing application========");
        GoogleCredential credential = GoogleCredential.getApplicationDefault();
        if (credential.createScopedRequired()) {
            credential = credential.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        }

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Cloudbilling cloudbillingService = new Cloudbilling.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Google Cloud Platform Sample")
                .build();

        Cloudbilling.BillingAccounts.List request = cloudbillingService.billingAccounts().list();
        ListBillingAccountsResponse response = request.execute();

        do {
            response = request.execute();
            if (response.getBillingAccounts() == null)
                continue;

            for (BillingAccount billingAccount : response.getBillingAccounts()) {
                System.out.println(billingAccount.getName());
            }

            request.setPageToken(response.getNextPageToken());
        } while (response.getNextPageToken() != null);
    }
}
