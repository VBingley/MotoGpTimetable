package nl.bingley.motogptimetable;

import android.content.Context;
import android.graphics.Color;
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
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
				this::parseResponse,
				error -> addTextRowToTable(new String[]{"Something went wrong!"}));

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

	private void parseResponse(String response) {
		table.removeAllViews();
		addTextRowToTable(new String[]{"POS","NUM","NAME","TIME","LAST-LAP","LEAD","GAP"});
		try {
			TimingSheet timingSheet = new ObjectMapper().readValue(response, TimingSheet.class);

			//Set view title
			Category category = timingSheet.lapTimes.getCategory();
			toolbar.setTitle(category.getName() + " | " + category.getRemaining() + " remaining");

			//Fill table
			riders = TimingSheetUtils.fillNewRiderList(riders, timingSheet.lapTimes.getRiders().values());
			riders.forEach(this::addRowToTable);
		} catch (JsonProcessingException e) {
			addTextRowToTable(new String[]{e.getMessage()});
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

	private void addRowToTable(Rider rider) {
		TableRow row = new TableRow(table.getContext());

		setRowBackgroundColor(row, rider);

		row.addView(createRiderTextView(row.getContext(), TimingSheetUtils.getRiderPositionString(rider)));
		row.addView(createRiderTextView(row.getContext(), String.valueOf(rider.getNumber())));
		row.addView(createRiderTextView(row.getContext(), rider.getName().charAt(0) + " " + rider.getSurname().substring(0,3)));
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
