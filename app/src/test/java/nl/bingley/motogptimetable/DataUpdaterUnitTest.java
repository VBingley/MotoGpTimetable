package nl.bingley.motogptimetable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.Rider;

public class DataUpdaterUnitTest {

    private final TableData tableData = new TableData();

    private final DataUpdater dataUpdater;

    public DataUpdaterUnitTest() {
        RequestQueue queue = Mockito.mock(RequestQueue.class);

        dataUpdater = new DataUpdater(queue, tableData);
    }

    @BeforeAll
    public static void BeforeAll() {
        MockedStatic<Log> mockedLog = Mockito.mockStatic(Log.class);
        mockedLog.when(() -> Log.isLoggable(Mockito.anyString(), Mockito.anyInt())).thenReturn(true);
        MockedStatic<TextUtils> mockedTextUtils = Mockito.mockStatic(TextUtils.class);
        mockedTextUtils.when(() -> TextUtils.isEmpty(Mockito.anyString())).thenReturn(false);
        MockedStatic<Uri> mockedUri = Mockito.mockStatic(Uri.class);
        mockedUri.when(() -> Uri.parse(Mockito.anyString())).thenReturn(null);
    }

    @Test
    public void TestHandleResponse() {
        dataUpdater.handleLiveTimingResponse(readResourceFileAsString("RaceFinished.json"));

        Category category = tableData.getCategory();
        assertEquals(19, category.getId());
        assertEquals("MotoE", category.getName());
        assertEquals("2024", category.getYear());
        assertEquals("F", category.getSessionStatus());
        assertEquals(0, category.getRemainingInt());
        assertEquals("null", category.getDuration());
        assertEquals(0, category.getDurationInt());

        Collection<Rider> riders = tableData.getRiders();
        assertEquals(17, riders.size());
    }

    @Test
    public void TestHandleResponseFollowUp() {
        String response = readResourceFileAsString("RaceFinished.json");
        dataUpdater.handleLiveTimingResponse(response);
        dataUpdater.handleLiveTimingResponse(response);

        Category category = tableData.getCategory();
        assertEquals(19, category.getId());
        assertEquals("MotoE", category.getName());
        assertEquals("2024", category.getYear());
        assertEquals("F", category.getSessionStatus());
        assertEquals(0, category.getRemainingInt());
        assertEquals("null", category.getDuration());
        assertEquals(0, category.getDurationInt());

        Collection<Rider> riders = tableData.getRiders();
        assertEquals(17, riders.size());
    }

    private String readResourceFileAsString(String fileName) {
        InputStream inputStream = Objects.requireNonNull(getClass().getClassLoader()).getResourceAsStream(fileName);
        if (inputStream == null) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return null;
        }
    }
}
