package se.mah.k3lara.skaneAPI.view;

import java.util.ArrayList;
import se.mah.k3lara.skaneAPI.model.Journey;
import se.mah.k3lara.skaneAPI.model.Journeys;
import se.mah.k3lara.skaneAPI.model.Station;
import se.mah.k3lara.skaneAPI.xmalparser.Parser;

public class TestClass {

	public static void main(String[] args) {
		System.out.println("// Results when searching for S�lvesborg resecentrum - Malm� Gustav Adolfs torg");
		String searchURL = Constants.getURL("80000","80100",20); //Malm� C = 80000,  Malm� GAtorg 80100, H�sleholm C 93070 Kolla sk�netrafiken f�r fler koder
		Journeys journeys = Parser.getJourneys(searchURL);
		for (Journey journey : journeys.getJourneys()) {
			System.out.print(journey.getStartStation()+" - ");
			System.out.print(journey.getEndStation());
			System.out.println(" Departs in "+journey.getTimeToDeparture()+ " minutes " + journey.getLineOnFirstJourney());
		} 
		
	   System.out.println("// Stations when searching for stations containing \"Malm\"");
		ArrayList<Station> searchStations = new ArrayList<Station>(); 
		searchStations.addAll(Parser.getStationsFromURL("Malm"));
		for (Station s: searchStations){
			System.out.println(s.getStationName());
		}

	}

}
