package nl.bingley.motogptimetable.model.livetiming;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LapTimes {

	@JsonProperty("head")
	private Category category;
	@JsonProperty("rider")
	private Map<Integer,Rider> riders;

	public Category getCategory() {
		return category;
	}

	public Map<Integer, Rider> getRiders() {
		return riders;
	}
}