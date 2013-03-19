package com.bixito.db;

import com.google.appengine.api.datastore.Text;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import static com.bixito.db.Constants.*;

public class DB {

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

	public static void updateStationsData() throws Exception {

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

		Text actualData = new Text(value);
		stations.setProperty(BIXI_DATA_DB_PROPERTY, actualData);

		datastore.put(stations);
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
		HttpURLConnection as = HttpUtility.sendPostRequest(
				Constants.STATS_SERVER, params);
		as.getExpiration();

	}
}
