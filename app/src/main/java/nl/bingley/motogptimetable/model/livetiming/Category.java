package nl.bingley.motogptimetable.model.livetiming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

	@JsonProperty("championship_id")
	private int id;
	@JsonProperty("category")
	private String name;
	@JsonProperty("date")
	private String year;
	@JsonProperty("session_status_id")
	private String sessionStatus;
	@JsonProperty("session_name")
	private String sessionName;
	@JsonProperty("duration")
	private String duration;
	@JsonProperty("remaining")
	private String remaining;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYear() {
		String[] split = year.split("/");
		return split[split.length-1];
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(String sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getRemaining() {
		return remaining;
	}

	public void setRemaining(String remaining) {
		this.remaining = remaining;
	}

	public SessionType getType() {
		if (sessionName.toLowerCase().contains("practice")) {
			return SessionType.Practice;
		} else if (sessionName.toLowerCase().contains("qualifying")) {
			return SessionType.Qualifying;
		} else if (sessionName.toLowerCase().contains("race")) {
			return SessionType.Race;
		} else {
			return SessionType.Unknown;
		}
	}
}