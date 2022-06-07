package nl.bingley.motogptimetable;

import java.time.LocalDateTime;
import java.util.Collection;

import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.Rider;

public class TimingSheetUtils {

    public static boolean isSessionStarted(Category category) {
        return "S".equals(category.getSessionStatus());
    }
}
