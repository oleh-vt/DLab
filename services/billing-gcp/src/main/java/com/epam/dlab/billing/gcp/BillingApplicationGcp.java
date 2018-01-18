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

import com.epam.dlab.billing.gcp.model.BillingConfigurationGcp;
import com.epam.dlab.billing.gcp.scheduler.BillingSchedulerGcp;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class BillingApplicationGcp {

    public static void main(String[] args) throws Exception {

        BillingConfigurationGcp config = BillingConfigurationGcp.builder().frequency(1)
                .initialDelay(0).timeUnit(TimeUnit.SECONDS).build();

        BillingSchedulerGcp billingSchedulerGcp = new BillingSchedulerGcp(config);
        billingSchedulerGcp.start();
        Thread.sleep(6000);
        billingSchedulerGcp.stop();
    }
}
