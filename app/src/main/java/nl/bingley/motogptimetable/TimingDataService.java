package nl.bingley.motogptimetable;

import nl.bingley.motogptimetable.model.Rider;
import nl.bingley.motogptimetable.model.TimingSheet;

public class TimingDataService {
	
	private static final String REST_URI = "https://www.motogp.com/en/json/live_timing/1";

    public TimingSheet getJsonLiveTiming() {
    	return null;
//        return client
//          .target(REST_URI)
//          .request(MediaType.APPLICATION_JSON)
//          .get(TimingSheet.class);
    }

	public int getLastRiderPosition(TimingSheet lastTimingSheet, int riderNumber) {
    	return lastTimingSheet.lapTimes.getRiders().values().stream()
    		.filter(rider -> rider.getNumber() == riderNumber)
    		.map(Rider::getPosition)
    		.findFirst().get();
    }
}