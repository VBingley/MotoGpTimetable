package nl.bingley.motogptimetable.model.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RiderCareer {

    @JsonProperty("number")
    private int riderNumber;
    @JsonProperty("team")
    private Team team;

    public int getRiderNumber() {
        return riderNumber;
    }

    public Team getTeam() {
        return team;
    }
}
