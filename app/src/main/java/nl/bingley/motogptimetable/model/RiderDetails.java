package nl.bingley.motogptimetable.model;

public class RiderDetails {

    private final int id;
    private final String teamName;
    private final String color;
    private final String textColor;

    public RiderDetails(int id, String teamName, String color, String textColor) {
        this.id = id;
        this.teamName = teamName;
        this.color = color;
        this.textColor = textColor;
    }

    public int getId() {
        return id;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getColor() {
        return color;
    }

    public String getTextColor() {
        return textColor;
    }
}
