package nl.bingley.motogptimetable.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimingSheet {
	@JsonProperty("lt")
	public LapTimes lapTimes;
	
	@Override
	public String toString() {
		return lapTimes.toString();
	}
}