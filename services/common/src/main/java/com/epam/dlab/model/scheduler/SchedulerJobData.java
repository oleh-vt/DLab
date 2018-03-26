package com.epam.dlab.model.scheduler;

import com.epam.dlab.dto.SchedulerJobDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SchedulerJobData {
    @JsonProperty
    private final String user;
    @JsonProperty("exploratory_name")
    private final String exploratoryName;
    @JsonProperty("scheduler_data")
    private final SchedulerJobDTO jobDTO;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public SchedulerJobData(@JsonProperty("user") String user,
							@JsonProperty("exploratory_name") String exploratoryName,
							@JsonProperty("scheduler_data") SchedulerJobDTO jobDTO) {
		this.user = user;
		this.exploratoryName = exploratoryName;
		this.jobDTO = jobDTO;
	}
}

