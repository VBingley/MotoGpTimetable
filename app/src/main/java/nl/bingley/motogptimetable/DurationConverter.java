package nl.bingley.motogptimetable;

public class DurationConverter {

	private int previousResponseDuration;
	private int duration;

	public DurationConverter(int currentDuration) {
		previousResponseDuration = currentDuration;
		duration = currentDuration;
	}

	public String getDurationString(int newResponseDuration) {
		updateDuration(newResponseDuration);
		return buildString();
	}

	private void updateDuration(int newResponseDuration) {
		if (previousResponseDuration == newResponseDuration) {
			duration--;
		} else {
			previousResponseDuration = newResponseDuration;
			duration = newResponseDuration;
		}
	}

	private String buildString() {
		String value = String.valueOf(duration/60);
		value += ':';
		int seconds = duration%60;
		if (seconds > 9) {
			value += seconds;
		} else {
			value += '0' + seconds;
		}
		return value;
	}
}
