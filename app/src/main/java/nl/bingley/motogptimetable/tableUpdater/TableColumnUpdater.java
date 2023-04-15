package nl.bingley.motogptimetable.tableUpdater;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import nl.bingley.motogptimetable.TableData;
import nl.bingley.motogptimetable.model.RiderDetails;
import nl.bingley.motogptimetable.model.livetiming.Rider;

public class TableColumnUpdater {

    public static TextView getPositionTextView(Rider rider, Context context) {
        String positionString = String.valueOf(rider.getPosition());
        positionString = positionString.equals("-1") ? "-" : positionString;
        positionString = positionString.length() == 1 ? " " + positionString : positionString;

        if (rider.isInPit()) {
            positionString = "P " + positionString;
        } else if (rider.hasRecentlyLostPosition()) {
            positionString = "v " + positionString;
        } else if (rider.hasRecentlyGainedPosition()) {
            positionString = "^ " + positionString;
        } else {
            positionString = "  " + positionString;
        }
        TextView positionTextView = TableUpdaterHelper.createRiderTextView(positionString, context);
        positionTextView.setTypeface(Typeface.MONOSPACE);
        positionTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return positionTextView;
    }

    public static TextView getNumberTextView(TableData tableData, Rider rider, @Nullable RiderDetails riderDetails, Context context) {
        TextView numberTextView;
        if (tableData.isColumnNumberTypeNumber() || riderDetails == null) {
            numberTextView = TableUpdaterHelper.createRiderTextView(String.valueOf(rider.getNumber()), context);
        } else {
            numberTextView = TableUpdaterHelper.createRiderTextView(riderDetails.getConstructor(), context);
        }
        if (riderDetails != null) {
            numberTextView.setBackgroundColor(Color.parseColor(riderDetails.getColor()));
            numberTextView.setTextColor(Color.parseColor(riderDetails.getTextColor()));
        }

        numberTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return numberTextView;
    }

    public static TextView getNameTextView(TableData tableData, Rider rider, @Nullable RiderDetails riderDetails, Context context) {
        String name;
        if (tableData.isColumnNameTypeLong() && riderDetails != null) {
            name = riderDetails.getName().charAt(0) + ". " + riderDetails.getSurname();
        } else if (tableData.isColumnNameTypeLong()) {
            name = rider.getName().charAt(0) + ". " + rider.getSurname().charAt(0) + rider.getSurname().substring(1).toLowerCase();
        } else {
            name = rider.getName().charAt(0) + " " + rider.getSurname().replace(" ", "").substring(0, 3);
        }
        return TableUpdaterHelper.createRiderTextView(name, context);
    }

    public static TextView getLapTimeTextView(TableData tableData, Rider rider, Context context) {
        String lapTime;
        if (rider.getPosition() == -1) {
            lapTime = "";
        } else if (tableData.isColumnLapTimeTypeBest()) {
            lapTime = rider.getBestTime();
        } else {
            lapTime = rider.getLastTime();
        }
        return TableUpdaterHelper.createRiderTextView(lapTime, context);
    }

    public static TextView getGapTextView(TableData tableData, Rider rider, Context context) {
        String gap;
        if (rider.getPosition() == -1) {
            gap = "";
        } else if (tableData.isColumnGapTypeLead()) {
            gap = rider.getLeadGap();
        } else {
            gap = rider.getPreviousGap();
        }
        return TableUpdaterHelper.createRiderTextView(gap, context);
    }
}
