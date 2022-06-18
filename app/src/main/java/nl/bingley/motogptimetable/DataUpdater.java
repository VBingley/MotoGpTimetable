package nl.bingley.motogptimetable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.bingley.motogptimetable.model.RiderDetails;
import nl.bingley.motogptimetable.model.details.RiderInfo;
import nl.bingley.motogptimetable.model.details.Season;
import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.ColumnType;
import nl.bingley.motogptimetable.model.livetiming.Rider;
import nl.bingley.motogptimetable.model.livetiming.SessionType;
import nl.bingley.motogptimetable.model.livetiming.TimingSheet;

public class DataUpdater extends Thread {

    private static final int positionTimeout = 15;
    private static final String liveTimingUrl = "https://www.motogp.com/en/json/live_timing/1";
    private static final String detailsBaseUrl = "https://api.motogp.com/riders-api/season/";
    private static final String seasonUrl = "/categories";
    private static final String riderInfoUrl = "/riders?category=";

    private final RequestQueue queue;
    private final TableData tableData;

    public DataUpdater(RequestQueue queue, TableData tableData) {
        this.queue = queue;
        this.tableData = tableData;
    }

    @Override
    public void run() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, liveTimingUrl,
                this::handleLiveTimingResponse,
                error -> tableData.setError("Error requesting live-timing data!"));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(2500, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    queue.add(stringRequest);
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tableData.setError("Error updating live-timing data! Updating has stopped!");
        }).start();
    }

    private void handleLiveTimingResponse(String response) {
        try {
            TimingSheet timingSheet = new ObjectMapper().readValue(response, TimingSheet.class);
            Category oldCategory = tableData.getCategory();
            Category newCategory = timingSheet.getLapTimes().getCategory();
            if (oldCategory == null || oldCategory.getType() != newCategory.getType() || !oldCategory.getName().equals(newCategory.getName())) {
                setSessionType(newCategory.getType());
                tableData.setCategory(newCategory);
                tableData.setRiders(new ArrayList<>());
                tableData.setRiderDetailsList(new ArrayList<>());
                tableData.setRiders(fillNewRiderList(tableData.getRiders(), timingSheet.getLapTimes().getRiders().values()));
                fetchRiderDetails();
            } else {
                tableData.setCategory(newCategory);
                tableData.setRiders(fillNewRiderList(tableData.getRiders(), timingSheet.getLapTimes().getRiders().values()));
            }
        } catch (JsonProcessingException e) {
            tableData.setError("Error processing live-timing data!");
        }
    }

    private void setSessionType(SessionType type) {
        switch (type) {
            case Practice:
            case Qualifying:
                tableData.setColumnLapTime(ColumnType.BestLapTime);
                tableData.setColumnGap(ColumnType.LeadGap);
                break;
            case Race:
                tableData.setColumnLapTime(ColumnType.LastLapTime);
                tableData.setColumnGap(ColumnType.NextGap);
                break;
        }
    }

    private void fetchRiderDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, detailsBaseUrl + tableData.getCategory().getYear() + seasonUrl,
                this::handleSeasonResponse, error -> tableData.setError("Error requesting championship data!"));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(2500, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void handleSeasonResponse(String response) {
        try {
            List<Season> seasons = new ObjectMapper().readValue(response, new TypeReference<List<Season>>() {
            });
            Category category = tableData.getCategory();
            Optional<Season> currentSeason = seasons.stream()
                    .filter(season -> season.getLegacyId() == category.getId())
                    .findFirst();
            if (currentSeason.isPresent()) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, detailsBaseUrl + category.getYear() + riderInfoUrl + currentSeason.get().getId(),
                        this::handleRiderInfoResponse, error -> tableData.setError("Error requesting rider details data!"));
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(2500, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(stringRequest);
            } else {
                tableData.setError("Team colors are not available for " + category.getName());
            }
        } catch (JsonProcessingException e) {
            tableData.setError("Error processing championship data!");
        }
    }

    private void handleRiderInfoResponse(String response) {
        try {
            List<RiderInfo> riderInfoList = new ObjectMapper().readValue(response, new TypeReference<List<RiderInfo>>() {
            });
            tableData.setRiderDetailsList(riderInfoList.stream()
                    .map(RiderDetails::new)
                    .collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            tableData.setError("Error processing rider details data!");
        }
    }

    public static Collection<Rider> fillNewRiderList(Collection<Rider> oldRiderList, Collection<Rider> newRiderList) {
        newRiderList.forEach(newRider -> oldRiderList.stream()
                .filter(oldRider -> oldRider.getNumber() == newRider.getNumber())
                .findAny().ifPresent(oldRider -> {
                    if (newRider.getPosition() != oldRider.getPosition()) {
                        // Position changed before timeout
                        newRider.setLastPosition(oldRider.getPosition());
                    } else if (newRider.getPosition() == oldRider.getPosition() && oldRider.getLastPositionChange().isAfter(LocalDateTime.now().minusSeconds(positionTimeout))) {
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
}
