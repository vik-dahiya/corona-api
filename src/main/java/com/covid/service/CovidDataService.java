package com.covid.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.covid.models.LocationStats;

@Service
public class CovidDataService {
	
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<LocationStats> allStats = new ArrayList<>();
	
	public List<LocationStats> getAllStats() {
		return allStats;
	}

	@PostConstruct
	//Tells Spring to execute this method every second
	@Scheduled(cron = "* * 1 * * *")
	public void fetchData() throws IOException, InterruptedException {
		//To resolve the concurrency issues so that somebody else 
		//getting the data from given uri it gets from allstats.
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(VIRUS_DATA_URL))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader reader = new StringReader(response.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
		for (CSVRecord record : records) {
			LocationStats locationStat = new LocationStats();
		    locationStat.setState(record.get("Province/State"));
		    locationStat.setCountry(record.get("Country/Region"));
		    int latestCases = Integer.parseInt(record.get(record.size() - 1));
		    int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
		    locationStat.setLatestTotalCases(latestCases);
		    locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
		    newStats.add(locationStat);
		}
		this.allStats = newStats;
	}

}
