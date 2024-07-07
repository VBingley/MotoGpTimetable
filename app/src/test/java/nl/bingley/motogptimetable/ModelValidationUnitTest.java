package nl.bingley.motogptimetable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.LapTimes;
import nl.bingley.motogptimetable.model.livetiming.Rider;

public class ModelValidationUnitTest {

    @Test
    public void TestRaceFinished() throws IOException {
        URL url = Objects.requireNonNull(getClass().getClassLoader()).getResource("RaceFinished.json");
        LapTimes lapTimes = new ObjectMapper().readValue(url, LapTimes.class);

        Category category = lapTimes.getCategory();
        assertEquals(19, category.getId());
        assertEquals("MotoE", category.getName());
        assertEquals("2024", category.getYear());
        assertEquals("F", category.getSessionStatus());
        assertEquals(0, category.getRemainingInt());
        assertEquals("null", category.getDuration());
        assertEquals(0, category.getDurationInt());

        Map<Integer, Rider> riders = lapTimes.getRiders();
        assertEquals(17, riders.size());
    }

    @Test
    public void TestCategory() {
        String categoryJson = "{ \"remaining\": \"6\", \"duration\": \"12\" }";
        String categoryJsonNull = "{ \"remaining\": null, \"duration\": null }";
        Category category;
        Category categoryNull;

        try {
            category = new ObjectMapper().readValue(categoryJson, Category.class);
            categoryNull = new ObjectMapper().readValue(categoryJsonNull, Category.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertEquals(6, category.getRemainingInt());
        assertEquals(12, category.getDurationInt());
        assertEquals(0, categoryNull.getRemainingInt());
        assertEquals(0, categoryNull.getDurationInt());
    }
}
