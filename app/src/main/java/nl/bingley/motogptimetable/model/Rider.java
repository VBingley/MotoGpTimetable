package nl.bingley.motogptimetable.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rider {
	@JsonProperty("rider_number")
	private int number;
	@JsonProperty("rider_name")
	private String name;
	@JsonProperty("rider_surname")
	private String surname;
	@JsonProperty("pos")
	private int position;
	@JsonProperty("lap_time")
	private String laptime;
	@JsonProperty("gap_first")
	private String leadGap;
	@JsonProperty("last_lap_time")
	private String lastTime;
	@JsonProperty("gap_prev")
	private String previousGap;

	private int lastPosition;
	private LocalDateTime lastPositionChange;

	public Rider() {
		lastPosition = -1;
		lastPositionChange = LocalDateTime.now();
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setLaptime(String laptime) {
		this.laptime = laptime;
	}
	
	public String getLaptime() {
		return laptime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	
	public String getLastTime() {
		return lastTime;
	}
	
	public void setLeadGap(String leadGap) {
		this.leadGap = leadGap;
	}
	
	public String getLeadGap() {
		if(leadGap.length() > 7) {
			return leadGap.substring(0,7);
		}
		return leadGap;
	}

	public void setPreviousGap(String previousGap) {
		this.previousGap = previousGap;
	}
	
	public String getPreviousGap() {
		if(previousGap.length() > 7) {
			return previousGap.substring(0,7);
		}
		return previousGap;
	}

	public String toString() {
		return String.join(" \t", Integer.toString(position), Integer.toString(number), getLaptime(), getLastTime(), getLeadGap(), getPreviousGap(), name.charAt(0) + " " + surname);
	}

	public int getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(int lastPosition) {
		this.lastPosition = lastPosition;
	}

	public LocalDateTime getLastPositionChange() {
		return lastPositionChange;
	}

	public void setLastPositionChange(LocalDateTime lastPositionChange) {
		this.lastPositionChange = lastPositionChange;
	}
}