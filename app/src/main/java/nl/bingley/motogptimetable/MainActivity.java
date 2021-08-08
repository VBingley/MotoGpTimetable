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
import nl.bingley.motogptimetable.model.Rider;
import nl.bingley.motogptimetable.model.TimingSheet;

import android.view.Menu;
import android.view.MenuItem;
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
				error -> showError("That didn't work!"));

		queue.add(stringRequest);
	}

	private void parseResponse(String response) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			TimingSheet timingSheet = mapper.readValue(response, TimingSheet.class);
			binding.toolbar.setTitle(timingSheet.lapTimes.getCategory().getName());
			timingSheet.lapTimes.getRiders()
					.forEach((pos, rider) -> binding.table.addView(createRow(rider)));
		} catch (JsonProcessingException e) {
			showError(e.getMessage());
		}
	}

	private void showError(String message) {
		TableRow row = new TableRow(binding.table.getContext());
		TextView text = new TextView(row.getContext());
		text.setText(message);
		row.addView(text);
	}

	private TableRow createRow(Rider rider) {
		TableRow row = new TableRow(binding.table.getContext());
		TextView text = new TextView(row.getContext());
		text.setText(rider.toString(rider.getPosition()));
		row.addView(text);
		return row;
	}
}