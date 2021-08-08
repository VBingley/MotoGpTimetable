package nl.bingley.motogptimetable;

import java.time.LocalDateTime;
import java.util.Collection;

import nl.bingley.motogptimetable.model.Rider;

public class TimingSheetUtils {

	public static Collection<Rider> fillNewRiderList(Collection<Rider> oldRiderList, Collection<Rider> newRiderList) {
		newRiderList.forEach(newRider -> oldRiderList.stream()
				.filter(oldRider -> oldRider.getNumber() == newRider.getNumber())
				.findAny().ifPresent(oldRider -> {
			if (newRider.getPosition() != oldRider.getPosition()) {
				// Position changed before timeout
				newRider.setLastPosition(oldRider.getPosition());
			} else if (newRider.getPosition() == oldRider.getPosition() && oldRider.getLastPositionChange().isAfter(LocalDateTime.now().minusSeconds(10))) { // 4 - 5
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
		if (hasLostPosition(rider)) {
			return "v " + rider.getPosition();
		} else if (hasGainedPosition(rider)) {
			return  "^ " + rider.getPosition();
		} else {
			return String.valueOf(rider.getPosition());
		}
	}

	public static boolean hasGainedPosition(Rider rider) {
		return rider.getLastPosition() > rider.getPosition() && rider.getLastPosition() != -1;
	}

	public static boolean hasLostPosition(Rider rider) {
		return rider.getLastPosition() < rider.getPosition() && rider.getLastPosition() != -1;
	}
}
