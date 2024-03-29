package nl.bingley.motogptimetable.model.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Season {

    @JsonProperty("id")
    private UUID id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("legacy_id")
    private int legacyId;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLegacyId() {
        return legacyId;
    }
}
