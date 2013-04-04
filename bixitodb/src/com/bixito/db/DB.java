package com.bixito.db;

import java.util.logging.Logger;
import com.google.appengine.api.datastore.Text;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mortbay.log.Log;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import static com.bixito.db.Constants.*;

public class DB {

	public static final Logger Log = Logger.getLogger(DB.class
			.getName());
	private static DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	public static String getStationsData() throws Exception {
		//
		// Query query = new Query(BIXI_DATA_DB_KIND);
		// List<Entity> results = datastore.prepare(query).asList(
		// FetchOptions.Builder.withDefaults());
		//
		Text actualData = null;

		Entity stations = getStationsEntity();
		// if (results.size() != 0) {
		// actualData = (Text)
		// results.get(0).getProperty(BIXI_DATA_DB_PROPERTY);
		// }
		if (stations != null) {
			actualData = (Text) stations.getProperty(BIXI_DATA_DB_PROPERTY);
			return actualData.getValue();
		}
		return "";

	}

	public static String[] updateStationsData() throws Exception {

		String[] returnVal = new String[2];
		returnVal[0] = getStationsData();

		Entity stations = getStationsEntity();
		String value = "";
		URL url = new URL(BIXI_DATA_URL);
		URLConnection yc = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null)
			value = value + inputLine;
		in.close();
		DB.Log.warning("inside updateStationsData() finished parinsg bixi xml");
		Text actualData = new Text(value);
		stations.setProperty(BIXI_DATA_DB_PROPERTY, actualData);

		datastore.put(stations);

		returnVal[1] = value;
		return returnVal;
	}

	private static Entity getStationsEntity() throws Exception {
		Entity stations = null;
		if (BIXI_DATA_KEY == null) {
			// check if we already have our entity in db
			Query query = new Query(BIXI_DATA_DB_KIND);
			List<Entity> results = datastore.prepare(query).asList(
					FetchOptions.Builder.withDefaults());
			if (results.size() > 0) {
				stations = results.get(0);
			} else {
				// first time, create the thing
				stations = new Entity(BIXI_DATA_DB_KIND);
				BIXI_DATA_KEY = stations.getKey();
			}
		} else {
			stations = datastore.get(BIXI_DATA_KEY);
		}

		return stations;
	}

	public static void addStatistics(String deviceId) throws Exception {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("datum[deviceId]", deviceId);
		params.put("datum[location]", "NotAvailable");
		params.put("datum[time]", GregorianCalendar.getInstance().getTime()
				.toString());
		HttpURLConnection r = HttpUtility.sendPostRequest(Constants.STATS_SERVER, params);
		r.getResponseCode();
	}

	public static void calculateStationsStats(String[] stats) throws Exception {

		
		List<BikeStation> oldStations = StationParser.parseStations(stats[0]);
		List<BikeStation> newStations = StationParser.parseStations(stats[1]);

		Map<Integer, Integer> popularity = new HashMap<Integer, Integer>();
		int numOfStations = oldStations.size();

		for (BikeStation s : oldStations) {
			// find the station in newStations,
			// build an index based on how many bikes were taken from most to
			// least changes
			// this index is the station popularity
			BikeStation newStation = null;
			for (BikeStation n : newStations) {
				if (s.getStationId() == n.getStationId()) {
					// found the station
					newStation = n;
					break;
				}
			}
			// get popularities
			int oldNumberOfBikes = s.getNbBikes();
			int newNumberOfBikes = newStation.getNbBikes();
			int popularityChange = 0;
			if (oldNumberOfBikes != newNumberOfBikes) {
				// popularity changes
				popularityChange = Math
						.abs(oldNumberOfBikes - newNumberOfBikes);
			}
			int p = s.getStationPopularity();
			newStation.setLocalPopularity(p);
			int pop = numOfStations - p
					+ popularityChange;
			int sId = s.getStationId();
			popularity.put(sId, pop);
		}
		ValueComparator bvc = new ValueComparator(popularity);
		TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer, Integer>(
				bvc);
		sorted_map.putAll(popularity);
		int rank = 0;
		for (Integer key : sorted_map.keySet()) {
			rank++;
			for (BikeStation b : newStations) {
				if (b.getStationId() == key) {
					b.setStationPopularity(rank);
					break;
				}
			}
		}

		Log.warning("Finished updating station stats. Rank should be updated now");
	}
	
}

class ValueComparator implements Comparator<Integer> {

	Map<Integer, Integer> base;

	public ValueComparator(Map<Integer, Integer> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(Integer a, Integer b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}