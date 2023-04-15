package nl.bingley.motogptimetable.model.livetiming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import nl.bingley.motogptimetable.tableUpdater.TableUpdaterHelper;

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
    private String bestTime;
    @JsonProperty("gap_first")
    private String leadGap;
    @JsonProperty("last_lap_time")
    private String lastTime;
    @JsonProperty("gap_prev")
    private String previousGap;
    @JsonProperty("on_pit")
    private String onPit;

    private boolean hasFastestLap;
    private positionChangeDirectionType positionChangeDirection;

    public enum positionChangeDirectionType {
        GAINED,
        LOST,
        DNF
    }

    private LocalDateTime lastPositionChange;
    private LocalDateTime lastBestTimeChange;

    public Rider() {
        hasFastestLap = false;
        positionChangeDirection = positionChangeDirectionType.GAINED;
        lastPositionChange = LocalDateTime.now().minusSeconds(TableUpdaterHelper.highlightTimeout);
        lastBestTimeChange = LocalDateTime.now().minusSeconds(TableUpdaterHelper.highlightTimeout);
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

    public String getBestTime() {
        return bestTime;
    }

    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public String getLeadGap() {
        if (leadGap.length() > 7) {
            return leadGap.substring(0, 7);
        }
        return leadGap;
    }

    public String getPreviousGap() {
        if (previousGap.length() > 7) {
            return previousGap.substring(0, 7);
        }
        return previousGap;
    }

    public boolean isInPit() {
        return "P".equalsIgnoreCase(onPit);
    }

    public boolean hasFastestLap() {
        return hasFastestLap;
    }

    public void setHasFastestLap(boolean hasFastestLap) {
        this.hasFastestLap = hasFastestLap;
    }

    public LocalDateTime getLastPositionChange() {
        return lastPositionChange;
    }

    public void setLastPositionChange(LocalDateTime lastPositionChange) {
        this.lastPositionChange = lastPositionChange;
    }

    public LocalDateTime getLastBestTimeChange() {
        return lastBestTimeChange;
    }

    public void setLastBestTimeChange(LocalDateTime lastBestTimeChange) {
        this.lastBestTimeChange = lastBestTimeChange;
    }

    public void setPositionChangeDirection(positionChangeDirectionType positionChangeDirection) {
        this.positionChangeDirection = positionChangeDirection;
    }

    public boolean hasRecentlyCrashed() {
        return positionChangeDirection == positionChangeDirectionType.DNF && isChangeRecent(lastPositionChange);
    }

    public boolean hasRecentlyGainedPosition() {
        return positionChangeDirection == positionChangeDirectionType.GAINED && isChangeRecent(lastPositionChange);
    }

    public boolean hasRecentlyLostPosition() {
        return positionChangeDirection == positionChangeDirectionType.LOST && isChangeRecent(lastPositionChange);
    }

    public boolean hasRecentlyImprovedBestTime() {
        return lastBestTimeChange.plusSeconds(TableUpdaterHelper.highlightTimeout).isAfter(LocalDateTime.now());
    }

    private boolean isChangeRecent(LocalDateTime lastChange) {
        return lastChange.plusSeconds(TableUpdaterHelper.highlightTimeout).isAfter(LocalDateTime.now());
    }
}