package nl.bingley.motogptimetable.model.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {

    @JsonProperty("name")
    private String name;
    @JsonProperty("constructor")
    private Constructor constructor;
    @JsonProperty("color")
    private String color;
    @JsonProperty("text_color")
    private String textColor;

    public String getName() {
        return name;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public String getColor() {
        return color;
    }

    public String getTextColor() {
        return textColor;
    }
}
