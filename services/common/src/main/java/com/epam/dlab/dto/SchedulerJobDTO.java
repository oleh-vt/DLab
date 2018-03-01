/*
 * Copyright (c) 2018, EPAM SYSTEMS INC
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

package com.epam.dlab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

/**
 * Stores info about a scheduler job (general duration, days to repeat, time to start and finish).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchedulerJobDTO {

    @JsonProperty("begin_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @JsonProperty("finish_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate finishDate;

    @JsonProperty("start_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

	@JsonProperty("end_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

	@JsonProperty("days_repeat")
	private List<DayOfWeek> daysRepeat = Collections.emptyList();

    @JsonProperty("timezone_offset")
    private ZoneOffset timeZoneOffset;

}