package com.epam.dlab.backendapi.resources.dto;

import com.epam.dlab.dto.exploratory.ImageStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageInfoRecord {
    private final String name;
    private final String description;
    private final String application;
    private final String fullName;
    private final ImageStatus status;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public ImageInfoRecord(@JsonProperty("name") String name, @JsonProperty("description") String description,
						   @JsonProperty("application") String application, @JsonProperty("fullName") String fullName,
						   @JsonProperty("status") ImageStatus status) {
		this.name = name;
		this.description = description;
		this.application = application;
		this.fullName = fullName;
		this.status = status;
	}
}
