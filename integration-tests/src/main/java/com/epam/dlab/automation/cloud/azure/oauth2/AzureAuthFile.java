package com.epam.dlab.automation.cloud.azure.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureAuthFile {
	@JsonProperty
	private String clientId;
	@JsonProperty
	private String clientSecret;
	@JsonProperty
	private String tenantId;
	@JsonProperty
	private String subscriptionId;

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}
}
