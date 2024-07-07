package nl.bingley.motogptimetable.tableUpdater;


import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import nl.bingley.motogptimetable.Constants;

public class TableUpdaterHelper {

    public static TextView createRiderTextView(String message, Context context) {
        TextView text = new TextView(context);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.TEXT_SIZE);
        text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        text.setText(message);
        return text;
    }

    public static void addTextRowToTable(TableLayout table, String[] messages) {
        TableRow row = new TableRow(table.getContext());
        for (String message : messages) {
            TextView text = new TextView(row.getContext());
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.TEXT_SIZE);
            text.setText(message);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(text);
        }
        table.addView(row);
    }
}
