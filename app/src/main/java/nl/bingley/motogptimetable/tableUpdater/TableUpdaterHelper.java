package nl.bingley.motogptimetable.tableUpdater;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableUpdaterHelper {

    public static final int highlightTimeout = 15;

    private static final int TEXT_SIZE = 16;

    public static final int GREEN = Color.argb(51, 0, 255, 0);
    public static final int RED = Color.argb(51, 255, 0, 0);
    public static final int ORANGE = Color.argb(51, 255, 165, 0);

    public static TextView createRiderTextView(String message, Context context) {
        TextView text = new TextView(context);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        text.setText(message);
        return text;
    }

    public static void addTextRowToTable(TableLayout table, String[] messages) {
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
}
