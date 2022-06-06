package nl.bingley.motogptimetable.model.livetiming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimingSheet {
	@JsonProperty("lt")
	private LapTimes lapTimes;

	public LapTimes getLapTimes() {
		return lapTimes;
	}
}