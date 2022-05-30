package nl.bingley.motogptimetable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.text.WordUtils;

import nl.bingley.motogptimetable.model.Category;
import nl.bingley.motogptimetable.model.Rider;
import nl.bingley.motogptimetable.model.SessionType;

public class TableRowUpdater {

	private final TableLayout table;

	public TableRowUpdater(TableLayout table) {
		this.table = table;
	}

	public void addTextRowToTable(String[] messages) {
		TableRow row = new TableRow(table.getContext());
		for (String message : messages) {
			TextView text = new TextView(row.getContext());
			text.setText(message);
			text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			row.addView(text);
		}
		table.addView(row);
	}

	private void addPracticeOrQualifyingRowToTable(Rider rider) {
		TableRow row = new TableRow(table.getContext());
		setRowBackgroundColor(row, rider);

		TextView positionTextView = createRiderTextView(row.getContext(), TimingSheetUtils.getRiderPositionString(rider));
		positionTextView.setTypeface(Typeface.MONOSPACE);
		row.addView(positionTextView);
		row.addView(createRiderTextView(row.getContext(), String.valueOf(rider.getNumber())));
		TextView nameTextView = createRiderTextView(row.getContext(),
				rider.getName().charAt(0) + " " + WordUtils.capitalizeFully(rider.getSurname()));
		row.addView(nameTextView);
		row.addView(createRiderTextView(row.getContext(), rider.getLaptime()));
		row.addView(createRiderTextView(row.getContext(), rider.getLeadGap()));
		row.addView(createRiderTextView(row.getContext(), rider.getLastTime()));

		table.addView(row);
	}

	private void addRaceRowToTable(Rider rider) {
		TableRow row = new TableRow(table.getContext());
		setRowBackgroundColor(row, rider);

		TextView positionTextView = createRiderTextView(row.getContext(), TimingSheetUtils.getRiderPositionString(rider));
		positionTextView.setTypeface(Typeface.MONOSPACE);
		row.addView(positionTextView);
		row.addView(createRiderTextView(row.getContext(), String.valueOf(rider.getNumber())));
		TextView nameTextView = createRiderTextView(row.getContext(),
				rider.getName().charAt(0) + " " + WordUtils.capitalizeFully(rider.getSurname()));
		row.addView(nameTextView);
		row.addView(createRiderTextView(row.getContext(), rider.getLastTime()));
		row.addView(createRiderTextView(row.getContext(), rider.getLeadGap()));
		row.addView(createRiderTextView(row.getContext(), rider.getPreviousGap()));

		table.addView(row);
	}

	public void addDefaultRowToTable(Rider rider) {
		TableRow row = new TableRow(table.getContext());
		setRowBackgroundColor(row, rider);

		// POS
		TextView positionTextView = createRiderTextView(row.getContext(), TimingSheetUtils.getRiderPositionString(rider));
		positionTextView.setTypeface(Typeface.MONOSPACE);
		row.addView(positionTextView);
		// NUM
		row.addView(createRiderTextView(row.getContext(), String.valueOf(rider.getNumber())));
		// NAME
		TextView nameTextView = createRiderTextView(row.getContext(), rider.getName().charAt(0) + " " + rider.getSurname().substring(0, 3));
		nameTextView.setTypeface(Typeface.MONOSPACE);
		row.addView(nameTextView);
		// LAPTIME
		row.addView(createRiderTextView(row.getContext(), rider.getLaptime()));
		// GAP
		row.addView(createRiderTextView(row.getContext(), rider.getLeadGap()));

		table.addView(row);
	}

	private TextView createRiderTextView(Context context, String message) {
		TextView text = new TextView(context);
		text.setText(message);
		text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
		return text;
	}

	private void setRowBackgroundColor(TableRow row, Rider rider) {
		if (TimingSheetUtils.hasGainedPosition(rider)) {
			row.setBackgroundColor(Color.argb(51,0,255,0));
		} else if (TimingSheetUtils.hasLostPosition(rider)) {
			row.setBackgroundColor(Color.argb(51,255,0,0));
		}
	}
}
