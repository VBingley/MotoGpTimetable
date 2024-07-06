package nl.bingley.motogptimetable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.bingley.motogptimetable.model.RiderDetails;
import nl.bingley.motogptimetable.model.details.RiderInfo;
import nl.bingley.motogptimetable.model.details.Season;
import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.ColumnType;
import nl.bingley.motogptimetable.model.livetiming.LapTimes;
import nl.bingley.motogptimetable.model.livetiming.Rider;
import nl.bingley.motogptimetable.model.livetiming.SessionType;

public class DataUpdater extends Thread {

    private static final String liveTimingUrl = "https://api.motogp.pulselive.com/motogp/v1/timing-gateway/livetiming-lite";
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
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, liveTimingUrl,
                    this::handleLiveTimingResponse,
                    error -> tableData.setError("Error requesting live-timing data! " + error.getMessage()));
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(2500, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            new Thread(() -> {
                while (!Thread.interrupted()) {
                    try {
                        queue.add(stringRequest);
                        Thread.sleep(2500L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                tableData.setError("Error updating live-timing data! Updating has stopped!");
            }).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleLiveTimingResponse(String response) {
        try {
            LapTimes lapTimes = new ObjectMapper().readValue(response, LapTimes.class);
            Category oldCategory = tableData.getCategory();
            Category newCategory = lapTimes.getCategory();
            if (oldCategory == null || oldCategory.getType() != newCategory.getType() || !oldCategory.getName().equals(newCategory.getName())) {
                setSessionType(newCategory.getType());
                tableData.setCategory(newCategory);
                tableData.setRiders(new ArrayList<>());
                tableData.setRiderDetailsList(new ArrayList<>());
                tableData.setRiders(fillNewRiderList(tableData.getRiders(), lapTimes.getRiders().values(), newCategory));
                fetchRiderDetails();
            } else {
                tableData.setCategory(newCategory);
                tableData.setRiders(fillNewRiderList(tableData.getRiders(), lapTimes.getRiders().values(), newCategory));
            }
        } catch (JsonProcessingException e) {
            tableData.setError("Error processing live-timing data! " + e.getMessage());
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
        } catch (Exception e) {
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

    public static Collection<Rider> fillNewRiderList(Collection<Rider> oldRiderList, Collection<Rider> newRiderList, Category category) {
        newRiderList.forEach(newRider -> oldRiderList.stream()
                .filter(oldRider -> oldRider.getNumber() == newRider.getNumber())
                .findAny().ifPresent(oldRider -> {
                    SetNewRiderPositionChanged(newRider, oldRider);
                    SetNewRiderBestTime(newRider, oldRider, category);
                }));
        SetRiderFastestLap(newRiderList);
        return newRiderList;
    }

    private static void SetNewRiderPositionChanged(Rider newRider, Rider oldRider) {
        newRider.setLastPositionChange(LocalDateTime.now());
        if (newRider.getPosition() == oldRider.getPosition()) {
            // Position didn't change, use previous timeout date
            newRider.setLastPositionChange(oldRider.getLastPositionChange());
            newRider.setPositionChangeDirection(oldRider.getPositionChangeDirection());
        } else if (newRider.getPosition() == -1) {
            // Crashed
            newRider.setPositionChangeDirection(Rider.positionChangeDirectionType.DNF);
        } else if (newRider.getPosition() < oldRider.getPosition() || oldRider.getPosition() == -1) {
            // Gained position
            newRider.setPositionChangeDirection(Rider.positionChangeDirectionType.GAINED);
        } else if (newRider.getPosition() > oldRider.getPosition()) {
            // Lost position
            newRider.setPositionChangeDirection(Rider.positionChangeDirectionType.LOST);
        }
    }

    private static void SetNewRiderBestTime(Rider newRider, Rider oldRider, Category category) {
        if (!category.isSessionFinished() && category.getType() == SessionType.Race && category.getRemainingInt() <= category.getNumLapsInt() - 2) {
            newRider.setBestTime("");
        } else {
            newRider.setLastBestTimeChange(LocalDateTime.now());
            List<String> lapTimes = new ArrayList<>();
            lapTimes.add(oldRider.getLastTime());
            lapTimes.add(oldRider.getBestTime());
            lapTimes.add(newRider.getLastTime());
            lapTimes.add(newRider.getBestTime());

            lapTimes = lapTimes.stream()
                    .sorted()
                    .filter(lapTime -> !lapTime.equals(""))
                    .filter(lapTime -> lapTime.length() == 8)
                    .collect(Collectors.toList());

            if (lapTimes.size() > 0) {
                newRider.setBestTime(lapTimes.get(0));
            }

            if (lapTimes.size() <= 2 || newRider.getBestTime().equals(oldRider.getBestTime())) {
                newRider.setLastBestTimeChange(oldRider.getLastBestTimeChange());
            }
        }
    }

    private static void SetRiderFastestLap(Collection<Rider> riders) {
        riders.stream()
                .filter(rider -> !rider.getBestTime().equals(""))
                .min(Comparator.comparing(Rider::getBestTime))
                .ifPresent(rider -> rider.setHasFastestLap(true));
    }
}
