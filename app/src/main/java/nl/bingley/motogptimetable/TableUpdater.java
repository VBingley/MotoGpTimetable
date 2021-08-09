package nl.bingley.motogptimetable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;

import androidx.appcompat.widget.Toolbar;
import nl.bingley.motogptimetable.model.Category;
import nl.bingley.motogptimetable.model.Rider;
import nl.bingley.motogptimetable.model.TimingSheet;

public class TableUpdater extends Thread {
	private static final String url = "https://www.motogp.com/en/json/live_timing/1";

	private final RequestQueue queue;
	private final TableLayout table;
	private final Toolbar toolbar;

	private Collection<Rider> riders = new ArrayList<>();

	public TableUpdater(Toolbar toolbar, TableLayout table, RequestQueue queue) {
		this.toolbar = toolbar;
		this.table = table;
		this.queue = queue;
	}

	@Override
	public void run() {
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
				this::handleResponse,
				error -> addTextRowToTable(new String[]{"Error"}));

		Thread thread = new Thread(() -> {
			while (!Thread.interrupted()) {
				queue.add(stringRequest);
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	private void handleResponse(String response) {
		table.removeAllViews();
		try {
			TimingSheet timingSheet = new ObjectMapper().readValue(response, TimingSheet.class);

			//Set view title
			Category category = timingSheet.lapTimes.getCategory();
			toolbar.setTitle(category.getName() + " | " + category.getRemaining() + " remaining");

			//Fill table
			addHeaderToTable(category);
			riders = TimingSheetUtils.fillNewRiderList(riders, timingSheet.lapTimes.getRiders().values());
			riders.forEach(rider -> addRiderToTable(category, rider));
		} catch (JsonProcessingException e) {
			addTextRowToTable(new String[]{"Error"});
		}
	}

	private void addTextRowToTable(String[] messages) {
		TableRow row = new TableRow(table.getContext());
		for (String message : messages) {
			TextView text = new TextView(row.getContext());
			text.setText(message);
			text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			row.addView(text);
		}
		table.addView(row);
	}

	private void addRiderToTable(Category category, Rider rider) {
		if (category.getSessionName().toLowerCase().contains("practice") || category.getSessionName().toLowerCase().contains("qualifying")) {
			addQualifyingRowToTable(rider);
		} else if (category.getSessionName().toLowerCase().contains("race")) {
			addRaceRowToTable(rider);
		} else {
			addDefaultRowToTable(rider);
		}
	}

	private void addHeaderToTable(Category category) {
		if (category.getSessionName().toLowerCase().contains("practice") || category.getSessionName().toLowerCase().contains("qualifying")) {
			addTextRowToTable(new String[]{"POS","NUM","NAME","BEST-LAP","LEAD-GAP","LAST-LAP"});
		} else if (category.getSessionName().toLowerCase().contains("race")) {
			addTextRowToTable(new String[]{"POS","NUM","NAME","LAST-LAP","LEAD-GAP","GAP"});
		} else {
			addTextRowToTable(new String[]{"POS","NUM","NAME","TIME","LAST-LAP","LEAD","GAP"});
		}
	}

	private void addQualifyingRowToTable(Rider rider) {
		TableRow row = new TableRow(table.getContext());
		setRowBackgroundColor(row, rider);

		TextView positionTextView = createRiderTextView(row.getContext(), TimingSheetUtils.getRiderPositionString(rider));
		positionTextView.setTypeface(Typeface.MONOSPACE);
		row.addView(positionTextView);
		row.addView(createRiderTextView(row.getContext(), String.valueOf(rider.getNumber())));
		TextView nameTextView = createRiderTextView(row.getContext(), rider.getName().charAt(0) + " " + rider.getSurname().substring(0, 3));
		nameTextView.setTypeface(Typeface.MONOSPACE);
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
		TextView nameTextView = createRiderTextView(row.getContext(), rider.getName().charAt(0) + " " + rider.getSurname().substring(0, 3));
		nameTextView.setTypeface(Typeface.MONOSPACE);
		row.addView(nameTextView);
		row.addView(createRiderTextView(row.getContext(), rider.getLastTime()));
		row.addView(createRiderTextView(row.getContext(), rider.getLeadGap()));
		row.addView(createRiderTextView(row.getContext(), rider.getPreviousGap()));

		table.addView(row);
	}

	private void addDefaultRowToTable(Rider rider) {
		TableRow row = new TableRow(table.getContext());
		setRowBackgroundColor(row, rider);

		TextView positionTextView = createRiderTextView(row.getContext(), TimingSheetUtils.getRiderPositionString(rider));
		positionTextView.setTypeface(Typeface.MONOSPACE);
		row.addView(positionTextView);
		row.addView(createRiderTextView(row.getContext(), String.valueOf(rider.getNumber())));
		TextView nameTextView = createRiderTextView(row.getContext(), rider.getName().charAt(0) + " " + rider.getSurname().substring(0, 3));
		nameTextView.setTypeface(Typeface.MONOSPACE);
		row.addView(nameTextView);
		row.addView(createRiderTextView(row.getContext(), rider.getLaptime()));
		row.addView(createRiderTextView(row.getContext(), rider.getLastTime()));
		row.addView(createRiderTextView(row.getContext(), rider.getLeadGap()));
		row.addView(createRiderTextView(row.getContext(), rider.getPreviousGap()));

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
