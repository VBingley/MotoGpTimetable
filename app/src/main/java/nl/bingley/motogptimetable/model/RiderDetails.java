package nl.bingley.motogptimetable.model;

import nl.bingley.motogptimetable.model.details.RiderInfo;

public class RiderDetails {

    private final int id;
    private final String name;
    private final String surname;
    private final String teamName;
    private final String constructor;
    private final String color;
    private final String textColor;

    public RiderDetails(RiderInfo riderInfo) {
        this.id = riderInfo.getLegacyId();
        this.name = riderInfo.getName();
        this.surname = riderInfo.getSurname();
        this.teamName = riderInfo.getCareer().getTeam().getName();
        this.constructor = riderInfo.getCareer().getTeam().getConstructor().getName();
        this.color = riderInfo.getCareer().getTeam().getColor();
        this.textColor = riderInfo.getCareer().getTeam().getTextColor();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getConstructor() {
        return constructor;
    }

    public String getColor() {
        return color;
    }

    public String getTextColor() {
        return textColor;
    }
}
