package nl.bingley.motogptimetable.model.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RiderInfo {

    @JsonProperty("legacy_id")
    private int legacyId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("surname")
    private String surname;
    @JsonProperty("current_career_step")
    private RiderCareer career;

    public int getLegacyId() {
        return legacyId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public RiderCareer getCareer() {
        return career;
    }
}
