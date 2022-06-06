package nl.bingley.motogptimetable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import nl.bingley.motogptimetable.model.RiderDetails;
import nl.bingley.motogptimetable.model.details.RiderInfo;
import nl.bingley.motogptimetable.model.details.Season;
import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.ColumnType;
import nl.bingley.motogptimetable.model.livetiming.SessionType;
import nl.bingley.motogptimetable.model.livetiming.TimingSheet;

public class DataUpdater extends Thread {

    private static final String liveTimingUrl = "https://www.motogp.com/en/json/live_timing/1";
    private static final String detailsBaseUrl = "https://api.motogp.com/riders-api/season/";
    private static final String seasonUrl = "/categories";
    private static final String riderInfoUrl = "/riders?category=";

    private final RequestQueue queue;
    private final TableUpdater tableUpdater;
    private final TableData tableData;

    public DataUpdater(RequestQueue queue, TableUpdater tableUpdater, TableData tableData) {
        this.queue = queue;
        this.tableUpdater = tableUpdater;
        this.tableData = tableData;
    }

    @Override
    public void run() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, liveTimingUrl,
                this::handleLiveTimingResponse,
                error -> tableUpdater.addTextRowToTable(new String[]{"Err1"}));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(2500, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    queue.add(stringRequest);
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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
                tableData.setRiders(TimingSheetUtils.fillNewRiderList(tableData.getRiders(), timingSheet.getLapTimes().getRiders().values()));
                fetchRiderDetails();
            } else {
                tableData.setCategory(newCategory);
                tableData.setRiders(TimingSheetUtils.fillNewRiderList(tableData.getRiders(), timingSheet.getLapTimes().getRiders().values()));
            }
            tableUpdater.refreshTable();
        } catch (JsonProcessingException e) {
            tableUpdater.addTextRowToTable(new String[]{"Err2"});
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, detailsBaseUrl + tableData.getCategory().getYear() + seasonUrl, this::handleSeasonResponse, null);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(2500, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void handleSeasonResponse(String response) {
        try {
            List<Season> seasons = new ObjectMapper().readValue(response, new TypeReference<List<Season>>() {
            });
            Category category = tableData.getCategory();
            seasons.stream()
                    .filter(season -> season.getLegacyId() == category.getId())
                    .findFirst()
                    .ifPresent(season -> {
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, detailsBaseUrl + category.getYear() + riderInfoUrl + season.getId(), this::handleRiderInfoResponse, null);
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(2500, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(stringRequest);
                    });
        } catch (JsonProcessingException e) {
            // Not that important
        }
    }

    private void handleRiderInfoResponse(String response) {
        try {
            List<RiderInfo> riderInfoList = new ObjectMapper().readValue(response, new TypeReference<List<RiderInfo>>() {
            });
            tableData.setRiderDetailsList(riderInfoList.stream()
                    .map(riderInfo -> new RiderDetails(
                            riderInfo.getLegacyId(),
                            riderInfo.getCareer().getTeam().getName(),
                            riderInfo.getCareer().getTeam().getColor(),
                            riderInfo.getCareer().getTeam().getTextColor()))
                    .collect(Collectors.toList()));
            tableUpdater.refreshTable();
        } catch (JsonProcessingException e) {
            // Not that important
        }
    }
}
