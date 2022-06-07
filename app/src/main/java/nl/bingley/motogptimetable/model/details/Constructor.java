package nl.bingley.motogptimetable.model.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Constructor {

    @JsonProperty("legacy_id")
    private int id;
    @JsonProperty("name")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
