package com.epam.dlab.model.library;

import com.epam.dlab.dto.exploratory.LibStatus;
import com.epam.dlab.model.ResourceType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Library {
	private final String group;
	private final String name;
	private final String version;
	private final LibStatus status;
	@JsonProperty("error_message")
	private final String errorMessage;
	private String resourceName;
	private ResourceType type;

	@JsonCreator
	public Library(@JsonProperty("group") String group, @JsonProperty("name") String name,
				   @JsonProperty("version") String version, @JsonProperty("status") LibStatus status,
				   @JsonProperty("error_message") String errorMessage) {
		this.group = group;
		this.name = name;
		this.version = version;
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public Library withType(ResourceType type) {
		setType(type);
		return this;
	}
	public Library withResourceName(String name) {
		setResourceName(name);
		return this;
	}
}
