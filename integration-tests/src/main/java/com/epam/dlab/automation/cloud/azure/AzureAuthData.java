package com.epam.dlab.automation.cloud.azure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureAuthData {
	@JsonProperty
	private String clientId;
	@JsonProperty
	private String clientSecret;
	@JsonProperty
	private String tenantId;
	@JsonProperty
	private String subscriptionId;
	@JsonProperty
	private String activeDirectoryEndpointUrl;
	@JsonProperty
	private String resourceManagerEndpointUrl;

	public String getActiveDirectoryEndpointUrl() {
		return activeDirectoryEndpointUrl;
	}

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

	public String getResourceManagerEndpointUrl() {
		return resourceManagerEndpointUrl;
	}
}
