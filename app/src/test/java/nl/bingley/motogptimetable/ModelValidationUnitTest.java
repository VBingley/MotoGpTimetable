package nl.bingley.motogptimetable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import nl.bingley.motogptimetable.model.livetiming.Category;

public class ModelValidationUnitTest {

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
