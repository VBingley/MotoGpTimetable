package nl.bingley.motogptimetable.model.livetiming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    @JsonProperty("championship_id")
    private int id;
    @JsonProperty("category")
    private String name = "Unknown";
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

    public String getName() {
        return name;
    }

    public String getYear() {
        String[] split = year.split("/");
        return split[split.length - 1];
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getDuration() {
        return duration;
    }

    public String getRemaining() {
        return remaining;
    }

    public SessionType getType() {
        if (sessionName == null) {
            return SessionType.Unknown;
        } else if (sessionName.toLowerCase().contains("practice")) {
            return SessionType.Practice;
        } else if (sessionName.toLowerCase().contains("qualifying")) {
            return SessionType.Qualifying;
        } else if (sessionName.toLowerCase().contains("race")) {
            return SessionType.Race;
        } else {
            return SessionType.Unknown;
        }
    }

    public boolean isSessionStarted() {
        return "S".equals(sessionStatus);
    }

    public boolean isSessionFinished() {
        return "0".equals(remaining);
    }
}