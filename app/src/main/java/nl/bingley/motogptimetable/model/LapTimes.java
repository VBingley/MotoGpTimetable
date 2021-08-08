package nl.bingley.motogptimetable.model;

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

	public void setCategory(Category category) {
		this.category = category;
	}

	public Map<Integer, Rider> getRiders() {
		return riders;
	}

	public void setRiders(Map<Integer, Rider> riders) {
		this.riders = riders;
	}

	@Override
	public String toString() {
		return riders.values().stream().map(Rider::toString).reduce("", (rider1, rider2) -> rider1 + "\n" + rider2);
	}
}