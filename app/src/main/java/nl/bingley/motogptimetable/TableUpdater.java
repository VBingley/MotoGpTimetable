package nl.bingley.motogptimetable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import nl.bingley.motogptimetable.model.RiderDetails;
import nl.bingley.motogptimetable.model.livetiming.Category;
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
        tableData.getRiders().forEach(this::addRiderRowToTable);
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
        String number = tableData.isColumnNumberTypeNumber() ? "Num" : "Team";
        String lapTime = tableData.isColumnLapTimeTypeBest() ? "Best Lap" : "Last Lap";
        String gap = tableData.isColumnGapTypeLead() ? "Lead" : "Gap";

        String[] headers = new String[]{"Pos", number, "Name", lapTime, gap};
        addTextRowToTable(headers);
    }

    public void addRiderRowToTable(Rider rider) {
        RiderDetails riderDetails = tableData.getRiderDetailsList().stream()
                .filter(riderD -> riderD.getId() == rider.getId())
                .findFirst().orElse(null);
        TableRow row = new TableRow(table.getContext());
        setRowBackgroundColor(row, rider);

        // POS
        TextView positionTextView = getPositionTextView(rider, row.getContext());
        row.addView(positionTextView);

        // NUM
        TextView numberTextView = getNumberTextView(rider, riderDetails, row.getContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 2, 2, 2);
        row.addView(numberTextView, params);

        // NAME
        TextView nameTextView = getNameTextView(rider, riderDetails, row.getContext());
        row.addView(nameTextView);

        // LAP-TIME
        TextView lapTimeTextView = getLapTimeTextView(rider, row.getContext());
        row.addView(lapTimeTextView);

        // GAP
        TextView gapTextView = getGapTextView(rider, row.getContext());
        row.addView(gapTextView);

        table.addView(row);
    }

    private TextView getPositionTextView(Rider rider, Context context) {
        String positionString = String.valueOf(rider.getPosition());
        positionString = positionString.equals("-1") ? "-" : positionString;
        positionString = positionString.length() == 1 ? " " + positionString : positionString;

        if (rider.isInPit()) {
            positionString = "P " + positionString;
        } else if (rider.hasLostPosition()) {
            positionString = "v " + positionString;
        } else if (rider.hasGainedPosition()) {
            positionString = "^ " + positionString;
        } else {
            positionString = "  " + positionString;
        }
        TextView positionTextView = createRiderTextView(positionString, context);
        positionTextView.setTypeface(Typeface.MONOSPACE);
        positionTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return positionTextView;
    }

    private TextView getNumberTextView(Rider rider, @Nullable RiderDetails riderDetails, Context context) {
        TextView numberTextView;
        if (tableData.isColumnNumberTypeNumber() || riderDetails == null) {
            numberTextView = createRiderTextView(String.valueOf(rider.getNumber()), context);
        } else {
            numberTextView = createRiderTextView(riderDetails.getConstructor(), context);
        }
        if (riderDetails != null) {
            numberTextView.setOnClickListener(v -> columnNumberClickHandler());
            numberTextView.setBackgroundColor(Color.parseColor(riderDetails.getColor()));
            numberTextView.setTextColor(Color.parseColor(riderDetails.getTextColor()));
        }
        numberTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return numberTextView;
    }

    private TextView getNameTextView(Rider rider, @Nullable RiderDetails riderDetails, Context context) {
        String name;
        if (tableData.isColumnNameTypeLong() && riderDetails != null) {
            name = riderDetails.getName().charAt(0) + ". " + riderDetails.getSurname();
        } else if (tableData.isColumnNameTypeLong()) {
            name = rider.getName().charAt(0) + ". " + rider.getSurname().charAt(0) + rider.getSurname().substring(1).toLowerCase();
        } else {
            name = rider.getName().charAt(0) + " " + rider.getSurname().replace(" ", "").substring(0, 3);
        }
        TextView nameTextView = createRiderTextView(name, context);
        nameTextView.setOnClickListener(v -> columnNameClickHandler());
        return nameTextView;
    }

    private TextView getLapTimeTextView(Rider rider, Context context) {
        String lapTime;
        if (rider.getPosition() == -1) {
            lapTime = "";
        } else if (tableData.isColumnLapTimeTypeBest()) {
            lapTime = rider.getLapTime();
        } else {
            lapTime = rider.getLastTime();
        }
        TextView lapTimeTextView = createRiderTextView(lapTime, context);
        lapTimeTextView.setOnClickListener(v -> columnLapTimeClickHandler());
        return lapTimeTextView;
    }

    private TextView getGapTextView(Rider rider, Context context) {
        String gap;
        if (rider.getPosition() == -1) {
            gap = "";
        } else if (tableData.isColumnGapTypeLead()) {
            gap = rider.getLeadGap();
        } else {
            gap = rider.getPreviousGap();
        }
        TextView gapTextView = createRiderTextView(gap, context);
        gapTextView.setOnClickListener(v -> columnGapClickHandler());
        return gapTextView;
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

    private TextView createRiderTextView(String message, Context context) {
        TextView text = new TextView(context);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        text.setText(message);
        return text;
    }

    private void setRowBackgroundColor(TableRow row, Rider rider) {
        if (rider.hasRecentlyCrashed()) {
            row.setBackgroundColor(Color.argb(51, 255, 165, 0));
        } else if (rider.hasGainedPosition()) {
            row.setBackgroundColor(Color.argb(51, 0, 255, 0));
        } else if (rider.hasLostPosition()) {
            row.setBackgroundColor(Color.argb(51, 255, 0, 0));
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
