package nl.bingley.motogptimetable;

import java.time.LocalDateTime;
import java.util.Collection;

import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.Rider;

public class TimingSheetUtils {

    private static final int POSITION_TIMEOUT = 15;

    public static Collection<Rider> fillNewRiderList(Collection<Rider> oldRiderList, Collection<Rider> newRiderList) {
        newRiderList.forEach(newRider -> oldRiderList.stream()
                .filter(oldRider -> oldRider.getNumber() == newRider.getNumber())
                .findAny().ifPresent(oldRider -> {
                    if (newRider.getPosition() != oldRider.getPosition()) {
                        // Position changed before timeout
                        newRider.setLastPosition(oldRider.getPosition());
                    } else if (newRider.getPosition() == oldRider.getPosition() && oldRider.getLastPositionChange().isAfter(LocalDateTime.now().minusSeconds(POSITION_TIMEOUT))) {
                        // Position change is recent, keep lastPosition
                        newRider.setLastPosition(oldRider.getLastPosition());
                        newRider.setLastPositionChange(oldRider.getLastPositionChange());
                    } else {
                        // Position hasn't changed
                        newRider.setLastPosition(newRider.getPosition());
                        newRider.setLastPositionChange(oldRider.getLastPositionChange());
                    }
                }));
        return newRiderList;
    }

    public static String getRiderPositionString(Rider rider) {
        String position = String.valueOf(rider.getPosition());
        if (position.equals("-1")) {
            return "- ";
        }

        if (position.length() == 1) {
            position = " " + position;
        }
        if (hasLostPosition(rider)) {
            return "v " + position + " ";
        } else if (hasGainedPosition(rider)) {
            return "^ " + position + " ";
        } else {
            return "  " + position + " ";
        }
    }

    public static boolean hasRecentlyCrashed(Rider rider) {
        return rider.getLastPosition() != -1 && rider.getPosition() == -1;
    }

    public static boolean hasGainedPosition(Rider rider) {
        return rider.getLastPosition() > rider.getPosition() && rider.getLastPosition() != -1;
    }

    public static boolean hasLostPosition(Rider rider) {
        return rider.getLastPosition() < rider.getPosition() && rider.getLastPosition() != -1;
    }

    public static boolean isSessionStarted(Category category) {
        return "S".equals(category.getSessionStatus());
    }
}
