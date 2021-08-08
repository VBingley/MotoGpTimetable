package nl.bingley.motogptimetable;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import nl.bingley.motogptimetable.model.Rider;
import nl.bingley.motogptimetable.model.TimingSheet;

public class TableUpdater extends Thread {

	private final TableLayout table;
	private final TimingDataService timingDataService;

	public TableUpdater(TableLayout table) {
		super();
		this.table = table;
		timingDataService = new TimingDataService();
	}

	@Override
	public void run() {
//		while (!this.isInterrupted()) {
//
//		}

	}
}
