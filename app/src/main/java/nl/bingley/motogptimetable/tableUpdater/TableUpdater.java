package nl.bingley.motogptimetable.tableUpdater;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import nl.bingley.motogptimetable.MainActivity;
import nl.bingley.motogptimetable.SessionRemainingCounter;
import nl.bingley.motogptimetable.TableData;
import nl.bingley.motogptimetable.model.RiderDetails;
import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.Rider;

public class TableUpdater extends Thread {

    private final Toolbar toolbar;
    private final TableLayout table;
    private final TableData tableData;
    private final SessionRemainingCounter sessionRemainingCounter;
    private final MainActivity activity;

    public TableUpdater(MainActivity activity, Toolbar toolbar, TableLayout table, TableData tableData) {
        this.activity = activity;
        this.toolbar = toolbar;
        this.table = table;
        this.tableData = tableData;
        sessionRemainingCounter = new SessionRemainingCounter(0);
    }

    @Override
    public void run() {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    activity.runOnUiThread(this::refreshTable);
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Snackbar.make(toolbar, "Error while updating the UI! Updating has stopped!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }).start();
    }

    public void refreshTable() {
        try {
            table.removeAllViews();
            setViewTitle(tableData.getCategory());
            addHeaderToTable();
            tableData.getRiders().forEach(this::addRiderRowToTable);

            if (tableData.hasError()) {
                Snackbar.make(toolbar, tableData.getError(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                tableData.setError(null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setViewTitle(Category category) {
        String title = category.getName();
        if (category.isSessionStarted()) {
            title += " | " + getSessionRemainingString(category);
        } else if (category.isSessionFinished()) {
            title += " | " + "Finished";
        } else {
            title += " | " + getPreSessionString(category);
        }
        toolbar.setTitle(title);
    }

    private String getSessionRemainingString(Category category) {
        switch (category.getType()) {
            case Practice:
            case Qualifying:
                int remaining = Integer.parseInt(category.getRemaining());
                return sessionRemainingCounter.getRemainingString(remaining) + " remaining";
            case Race:
                return category.getRemaining() + "/" + category.getDuration() + " laps remaining";
            default:
                return "";
        }
    }

    private String getPreSessionString(Category category) {
        switch (category.getType()) {
            case Practice:
            case Qualifying:
                int remaining = Integer.parseInt(category.getRemaining());
                return sessionRemainingCounter.buildString(remaining);
            case Race:
                return category.getDuration() + " laps";
            default:
                return "";
        }
    }

    private void addHeaderToTable() {
        String number = tableData.isColumnNumberTypeNumber() ? "Num" : "Team";
        String lapTime = tableData.isColumnLapTimeTypeBest() ? "Best Lap" : "Last Lap";
        String gap = tableData.isColumnGapTypeLead() ? "Lead" : "Gap";

        String[] headers = new String[]{"Pos", number, "Name", lapTime, gap};
        TableUpdaterHelper.addTextRowToTable(table, headers);
    }

    public void addRiderRowToTable(Rider rider) {
        RiderDetails riderDetails = tableData.getRiderDetailsList().stream()
                .filter(riderD -> riderD.getId() == rider.getId())
                .findFirst().orElse(null);
        TableRow row = new TableRow(table.getContext());
        row.setPadding(0, 2, 0, 2);
        setRowBackgroundColor(row, rider);

        // POS
        TextView positionTextView = TableColumnUpdater.getPositionTextView(rider, row.getContext());
        row.addView(positionTextView);

        // NUM
        TextView numberTextView = TableColumnUpdater.getNumberTextView(tableData, rider, riderDetails, row.getContext());
        numberTextView.setOnClickListener(v -> columnNumberClickHandler());
        row.addView(numberTextView);

        // NAME
        TextView nameTextView = TableColumnUpdater.getNameTextView(tableData, rider, riderDetails, row.getContext());
        nameTextView.setOnClickListener(v -> columnNameClickHandler());
        row.addView(nameTextView);

        // LAP-TIME
        TextView lapTimeTextView = TableColumnUpdater.getLapTimeTextView(tableData, rider, row.getContext());
        lapTimeTextView.setOnClickListener(v -> columnLapTimeClickHandler());
        row.addView(lapTimeTextView);

        // GAP
        TextView gapTextView = TableColumnUpdater.getGapTextView(tableData, rider, row.getContext());
        gapTextView.setOnClickListener(v -> columnGapClickHandler());
        row.addView(gapTextView);

        table.addView(row);
    }

    private void setRowBackgroundColor(TableRow row, Rider rider) {
        if (rider.hasRecentlyCrashed()) {
            row.setBackgroundColor(TableUpdaterHelper.ORANGE);
        } else if (rider.hasRecentlyGainedPosition()) {
            row.setBackgroundColor(TableUpdaterHelper.GREEN);
        } else if (rider.hasRecentlyLostPosition()) {
            row.setBackgroundColor(TableUpdaterHelper.RED);
        }
    }

    private void columnNumberClickHandler() {
        tableData.toggleColumnNumber();
        refreshTable();
    }

    private void columnNameClickHandler() {
        tableData.toggleColumnName();
        refreshTable();
    }

    private void columnLapTimeClickHandler() {
        tableData.toggleColumnLapTime();
        refreshTable();
    }

    private void columnGapClickHandler() {
        tableData.toggleColumnGap();
        refreshTable();
    }
}
