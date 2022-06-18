package nl.bingley.motogptimetable;

public class SessionRemainingCounter {

    private int previousRemaining;
    private int remaining;

    public SessionRemainingCounter(int currentDuration) {
        previousRemaining = currentDuration;
        remaining = currentDuration;
    }

    public String getRemainingString(int newRemaining) {
        updateRemaining(newRemaining);
        return buildString(remaining);
    }

    private void updateRemaining(int newResponseDuration) {
        if (previousRemaining == newResponseDuration) {
            remaining--;
        } else {
            previousRemaining = newResponseDuration;
            remaining = newResponseDuration;
        }
    }

    public String buildString(int remainingSeconds) {
        String value = String.valueOf(remainingSeconds / 60);
        value += ':';
        int seconds = remainingSeconds % 60;
        if (seconds > 9) {
            value += seconds;
        } else {
            value += '0' + seconds;
        }
        return value;
    }
}
