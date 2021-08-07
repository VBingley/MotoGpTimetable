package nl.bingley.motogptimetable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import nl.bingley.motogptimetable.model.Rider;
import nl.bingley.motogptimetable.model.TimingSheet;

public class TimingDataService {
	
	private static final String REST_URI = "https://www.motogp.com/en/json/live_timing/1";
    private final Client client = ClientBuilder.newClient();

    public TimingSheet getJsonLiveTiming() {
        return client
          .target(REST_URI)
          .request(MediaType.APPLICATION_JSON)
          .get(TimingSheet.class);
    }

	public int getLastRiderPosition(TimingSheet lastTimingSheet, int riderNumber) {
    	return lastTimingSheet.lapTimes.getRiders().values().stream()
    		.filter(rider -> rider.getNumber() == riderNumber)
    		.map(Rider::getPosition)
    		.findFirst().get();
    }
}