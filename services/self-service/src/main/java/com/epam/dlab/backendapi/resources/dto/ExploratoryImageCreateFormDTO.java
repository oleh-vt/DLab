package com.epam.dlab.backendapi.resources.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class ExploratoryImageCreateFormDTO {

    @NotBlank
    @JsonProperty("exploratory_name")
    private String notebookName;
    @NotBlank
    private final String name;
    private final String description;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public ExploratoryImageCreateFormDTO(@JsonProperty("exploratory_name") String notebookName,
										 @JsonProperty("name") String name,
										 @JsonProperty("description") String description) {
		this.notebookName = notebookName;
		this.name = name;
		this.description = description;
	}
}
