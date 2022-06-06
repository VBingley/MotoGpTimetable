package nl.bingley.motogptimetable.model.livetiming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rider {

	@JsonProperty("rider_id")
	private int id;
	@JsonProperty("rider_number")
	private int number;
	@JsonProperty("rider_name")
	private String name;
	@JsonProperty("rider_surname")
	private String surname;
	@JsonProperty("pos")
	private int position;
	@JsonProperty("lap_time")
	private String lapTime;
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

	public int getId() {
		return id;
	}
	
	public int getNumber() {
		return number;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public int getPosition() {
		return position;
	}
	
	public String getLapTime() {
		return lapTime;
	}
	
	public String getLastTime() {
		return lastTime;
	}
	
	public String getLeadGap() {
		if(leadGap.length() > 7) {
			return leadGap.substring(0,7);
		}
		return leadGap;
	}
	
	public String getPreviousGap() {
		if(previousGap.length() > 7) {
			return previousGap.substring(0,7);
		}
		return previousGap;
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