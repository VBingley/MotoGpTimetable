package nl.bingley.motogptimetable.model;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

public class Main {
	
	private static final String REST_URI = "https://www.motogp.com/en/json/live_timing/1";
    private final Client client = ClientBuilder.newClient();
    private TimingSheet lastTimingSheet;
	
//	public static void main(String[] args) throws InterruptedException {
//		Main main = new Main();
//		main.runTerminal();
//	}
	
	public void runTerminal() throws InterruptedException {
		lastTimingSheet = getJsonLiveTiming();
		printTimingSheet(lastTimingSheet.toString());
		Thread.sleep(5000);
		
		while(true) {
		TimingSheet newTimingSheet = getJsonLiveTiming();
		String newLapTimes = newTimingSheet.lapTimes.getRiders().values().stream()
			.map(rider -> rider.toString(getLastRiderPosition(rider.getNumber())))
			.reduce("", (rider1,rider2) -> rider1 + "\n" + rider2);
		
		printTimingSheet(newLapTimes);
		lastTimingSheet = newTimingSheet;
		Thread.sleep(5000);
		}
	}
	
	private void printTimingSheet(String timingSheet) {
		clearScreen();
		System.out.println(lastTimingSheet.lapTimes.getCategory() + "\n");
		System.out.print("POS\tNUM\tTIME\t\tLAST TIME\tLEAD\tGAP\tNAME");
		System.out.println(timingSheet);
	}

    private TimingSheet getJsonLiveTiming() {
        return client
          .target(REST_URI)
          .request(MediaType.APPLICATION_JSON)
          .get(TimingSheet.class);
    }
    
    private void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
    }
    
    private int getLastRiderPosition(int riderNumber) {
    	return lastTimingSheet.lapTimes.getRiders().values().stream()
    		.filter(rider -> rider.getNumber() == riderNumber)
    		.map(Rider::getPosition)
    		.findFirst().get();
    }
}