package nl.bingley.motogptimetable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import nl.bingley.motogptimetable.model.Category;
import nl.bingley.motogptimetable.model.ColumnType;
import nl.bingley.motogptimetable.model.SessionType;
import nl.bingley.motogptimetable.model.TimingSheet;

public class DataUpdater extends Thread {

    private static final String url = "https://www.motogp.com/en/json/live_timing/1";

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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                this::handleResponse,
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

    private void handleResponse(String response) {
        try {
            TimingSheet timingSheet = new ObjectMapper().readValue(response, TimingSheet.class);
            Category oldCategory = tableData.getCategory();
            Category newCategory = timingSheet.lapTimes.getCategory();
            if (oldCategory == null || oldCategory.getType() != newCategory.getType() || !oldCategory.getName().equals(newCategory.getName())) {
                setSessionType(newCategory.getType());
                tableData.setRiders(new ArrayList<>());
            }
            tableData.setCategory(newCategory);
            tableData.setRiders(TimingSheetUtils.fillNewRiderList(tableData.getRiders(), timingSheet.lapTimes.getRiders().values()));
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
}
