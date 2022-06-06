package nl.bingley.motogptimetable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import androidx.appcompat.widget.Toolbar;

import nl.bingley.motogptimetable.model.RiderDetails;
import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.ColumnType;
import nl.bingley.motogptimetable.model.livetiming.Rider;

public class TableUpdater {

    private static final int TEXT_SIZE = 16;

    private final Toolbar toolbar;
    private final TableLayout table;
    private final TableData tableData;
    private final DurationConverter durationConverter;

    public TableUpdater(Toolbar toolbar, TableLayout table, TableData tableData) {
        this.toolbar = toolbar;
        this.table = table;
        this.tableData = tableData;
        durationConverter = new DurationConverter(0);
    }

    public void refreshTable() {
        table.removeAllViews();
        setViewTitle(tableData.getCategory());
        addHeaderToTable();
        tableData.getRiders().forEach(this::addRowToTable);
    }

    private void setViewTitle(Category category) {
        String title = category.getName();
        if (TimingSheetUtils.isSessionStarted(category)) {
            title += " | " + getSessionRemainingString(category);
        } else if (category.getRemaining().equals("0")) {
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
                return durationConverter.getDurationString(remaining) + " remaining";
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
                return durationConverter.getDurationString(remaining);
            case Race:
                return category.getDuration() + " laps";
            default:
                return "";
        }
    }

    private void addHeaderToTable() {
        List<String> messages = new ArrayList<>(Arrays.asList("Pos", "Num", "Name"));
        String lapTime = tableData.getColumnLapTime() == ColumnType.BestLapTime ? "Best Lap" : "Last Lap";
        messages.add(lapTime);
        String gap = tableData.getColumnGap() == ColumnType.LeadGap ? "Lead" : "Gap";
        messages.add(gap);
        addTextRowToTable(messages.toArray(new String[0]));
    }

    public void addRowToTable(Rider rider) {
        Optional<RiderDetails> riderDetails = tableData.getRiderDetailsList().stream()
                .filter(riderD -> riderD.getId() == rider.getId())
                .findFirst();
        TableRow row = new TableRow(table.getContext());
        setRowBackgroundColor(row, rider);

        // POS
        TextView positionTextView = createRiderTextView(row.getContext(), TimingSheetUtils.getRiderPositionString(rider), ColumnType.Position);
        positionTextView.setTypeface(Typeface.MONOSPACE);
        row.addView(positionTextView);

        // NUM
        TextView numTextView = createRiderTextView(row.getContext(), String.valueOf(rider.getNumber()), ColumnType.Number);
        numTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        riderDetails.ifPresent(riderD -> {
            numTextView.setBackgroundColor(Color.parseColor(riderD.getColor()));
            numTextView.setTextColor(Color.parseColor(riderD.getTextColor()));
        });
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 2, 2, 2);
        row.addView(numTextView, params);

        // NAME
        String name;
        if (tableData.isColumnNameTypeLong()) {
            name = rider.getName().charAt(0) + ". " + rider.getSurname().charAt(0) + rider.getSurname().substring(1).toLowerCase();
            if (riderDetails.isPresent()) {
                name = riderDetails.get().getName().charAt(0) + ". " + riderDetails.get().getSurname();
            }
        } else {
            name = rider.getName().charAt(0) + " " + rider.getSurname().replace(" ", "").substring(0, 3);
        }
        TextView nameTextView = createRiderTextView(row.getContext(), name, tableData.getColumnName());
        row.addView(nameTextView);

        // LAPTIME
        String lapTime;
        if (rider.getPosition() == -1) {
            lapTime = "";
        } else if (tableData.isColumnLapTimeTypeBest()) {
            lapTime = rider.getLapTime();
        } else {
            lapTime = rider.getLastTime();
        }
        row.addView(createRiderTextView(row.getContext(), lapTime, tableData.getColumnLapTime()));

        // GAP
        String gap;
        if (rider.getPosition() == -1) {
            gap = "";
        } else if (tableData.isColumnGapTypeLead()) {
            gap = rider.getLeadGap();
        } else {
            gap = rider.getPreviousGap();
        }
        row.addView(createRiderTextView(row.getContext(), gap, tableData.getColumnGap()));

        table.addView(row);
    }

    public void addTextRowToTable(String[] messages) {
        TableRow row = new TableRow(table.getContext());
        for (String message : messages) {
            TextView text = new TextView(row.getContext());
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
            text.setText(message);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(text);
        }
        table.addView(row);
    }

    private TextView createRiderTextView(Context context, String message, ColumnType columnType) {
        TextView text = new TextView(context);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        text.setText(message);
        text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        switch (columnType) {
            case LongName:
            case ShortName:
                text.setOnClickListener(view -> columnNameClickListener());
                break;
            case BestLapTime:
            case LastLapTime:
                text.setOnClickListener(view -> columnLapTimeClickListener());
                break;
            case LeadGap:
            case NextGap:
                text.setOnClickListener(view -> columnGapClickListener());
                break;
        }

        return text;
    }

    private void setRowBackgroundColor(TableRow row, Rider rider) {
        if (TimingSheetUtils.hasRecentlyCrashed(rider)) {
            row.setBackgroundColor(Color.argb(51, 255, 165, 0));
        } else if (TimingSheetUtils.hasGainedPosition(rider)) {
            row.setBackgroundColor(Color.argb(51, 0, 255, 0));
        } else if (TimingSheetUtils.hasLostPosition(rider)) {
            row.setBackgroundColor(Color.argb(51, 255, 0, 0));
        }
    }

    private void columnNameClickListener() {
        tableData.toggleColumnName();
        refreshTable();
    }

    private void columnLapTimeClickListener() {
        tableData.toggleColumnLapTime();
        refreshTable();
    }

    private void columnGapClickListener() {
        tableData.toggleColumnGap();
        refreshTable();
    }
}
