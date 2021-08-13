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

	private final TableRowUpdater tableRowUpdater;

	private final RequestQueue queue;
	private final TableLayout table;
	private final Toolbar toolbar;
	private final DurationConverter durationConverter;

	private Collection<Rider> riders = new ArrayList<>();

	public TableUpdater(Toolbar toolbar, TableLayout table, RequestQueue queue) {
		this.toolbar = toolbar;
		this.table = table;
		this.queue = queue;
		tableRowUpdater = new TableRowUpdater(table);
		durationConverter = new DurationConverter(0);
	}

	@Override
	public void run() {
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
				this::handleResponse,
				error -> tableRowUpdater.addTextRowToTable(new String[]{"Error requesting data"}));

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
			Category category = timingSheet.lapTimes.getCategory();

			setViewTitle(category);

			addHeaderToTable(category);
			riders = TimingSheetUtils.fillNewRiderList(riders, timingSheet.lapTimes.getRiders().values());
			riders.forEach(rider -> tableRowUpdater.addRiderToTable(category, rider));
		} catch (JsonProcessingException e) {
			tableRowUpdater.addTextRowToTable(new String[]{"Error handling response"});
		}
	}

	private void setViewTitle(Category category) {
		String title = category.getName();
		if (TimingSheetUtils.isSessionStarted(category)) {
			title += " | " + getSessionRemainingString(category);
		} else if (category.getRemaining().equals("0")) {
			title += " | " + "Finished";
		} else {
			title += " | Not started";
		}
		toolbar.setTitle(title);
	}

	private String getSessionRemainingString(Category category) {
		if (TimingSheetUtils.isSessionPracticeOrQualifying(category)) {
			int remaining = Integer.parseInt(category.getRemaining());
			return durationConverter.getDurationString(remaining) + " remaining";
		} else if (TimingSheetUtils.isSessionRace(category)) {
			return  category.getRemaining() + " laps remaining";
		}
		return "";
	}

	private void addHeaderToTable(Category category) {
		if (TimingSheetUtils.isSessionPracticeOrQualifying(category)) {
			tableRowUpdater.addTextRowToTable(new String[]{"POS","NUM","NAME","BEST-LAP","LEAD-GAP","LAST-LAP"});
		} else if (TimingSheetUtils.isSessionRace(category)) {
			tableRowUpdater.addTextRowToTable(new String[]{"POS","NUM","NAME","LAST-LAP","LEAD-GAP","GAP"});
		} else {
			tableRowUpdater.addTextRowToTable(new String[]{"POS","NUM","NAME","TIME","LAST-LAP","LEAD","GAP"});
		}
	}
}
