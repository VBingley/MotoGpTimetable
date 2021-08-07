package nl.bingley.motogptimetable;

import nl.bingley.motogptimetable.model.TimingSheet;

public class MainTerminal {

	private final TimingDataService timingDataService;
	private TimingSheet lastTimingSheet;

	public MainTerminal() {
		timingDataService = new TimingDataService();
	}

	public static void main(String[] args) throws InterruptedException {
		MainTerminal mainTerminal = new MainTerminal();
		mainTerminal.runTerminal();
	}

	public void runTerminal() throws InterruptedException {
		lastTimingSheet = timingDataService.getJsonLiveTiming();
		printTimingSheet(lastTimingSheet.toString());
		Thread.sleep(5000);

		while(true) {
			TimingSheet newTimingSheet = timingDataService.getJsonLiveTiming();
			String newLapTimes = newTimingSheet.lapTimes.getRiders().values().stream()
					.map(rider -> rider.toString(timingDataService.getLastRiderPosition(lastTimingSheet,rider.getNumber())))
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

	private void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}
}
