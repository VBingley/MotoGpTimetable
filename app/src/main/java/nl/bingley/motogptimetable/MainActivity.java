package nl.bingley.motogptimetable;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import nl.bingley.motogptimetable.databinding.ActivityMainBinding;
import nl.bingley.motogptimetable.model.Category;
import nl.bingley.motogptimetable.model.Rider;
import nl.bingley.motogptimetable.model.TimingSheet;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	private AppBarConfiguration appBarConfiguration;
	private ActivityMainBinding binding;

	public MainActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		setSupportActionBar(binding.toolbar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		RequestQueue queue = Volley.newRequestQueue(this);
		String url ="https://www.motogp.com/en/json/live_timing/1";

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
		binding.table.removeAllViews();
		ObjectMapper mapper = new ObjectMapper();
		addTextRowToTable(new String[]{"POS","NUM","NAME","TIME","LAST TIME","LEAD","GAP"});
		try {
			TimingSheet timingSheet = mapper.readValue(response, TimingSheet.class);
			Category category = timingSheet.lapTimes.getCategory();
			binding.toolbar.setTitle(category.getName() + " | " + category.getRemaining() + " remaining");
			timingSheet.lapTimes.getRiders()
					.forEach((pos, rider) -> addRowToTable(rider));
		} catch (JsonProcessingException e) {
			addTextRowToTable(new String[]{e.getMessage()});
		}
	}

	private void addTextRowToTable(String[] messages) {
		TableRow row = new TableRow(binding.table.getContext());
		for (String message : messages) {
			TextView text = new TextView(row.getContext());
			text.setText(message);
			text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			row.addView(text);
		}
		binding.table.addView(row);
	}

	private void addRowToTable(Rider rider) {
		TableRow row = new TableRow(binding.table.getContext());

		addTextView(row, rider.getPositionString(rider.getPosition()));
		addTextView(row, String.valueOf(rider.getNumber()));
		addTextView(row, rider.getName().charAt(0) + " " + rider.getSurname().substring(0,3));
		addTextView(row, rider.getLaptime());
		addTextView(row, rider.getLastTime());
		addTextView(row, rider.getLeadGap());
		addTextView(row, rider.getPreviousGap());

		binding.table.addView(row);
	}

	private void addTextView(TableRow row, String message) {
		TextView text = new TextView(row.getContext());
		text.setText(message);
		text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
		row.addView(text);
	}
}