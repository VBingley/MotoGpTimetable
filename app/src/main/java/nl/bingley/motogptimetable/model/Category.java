package nl.bingley.motogptimetable.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

	@JsonProperty("category")
	private String name;
	@JsonProperty("duration")
	private int duration;
	@JsonProperty("remaining")
	private int remaining;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getRemaining() {
		return remaining;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	@Override
	public String toString() {
		return name + "\tTotal: " + duration + "\tRemaining:" + remaining;
	}
}