package com.bixito.db;

import com.google.appengine.api.datastore.Text;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query;

public class DB {
	
	static Key key = null;

	public static String getStationsData() {

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		

		Query query = new Query("BikesData");
		List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		
		Text t = null;
		
		if(results.size() != 0){
			t = (Text) results.get(0).getProperty("data");
		}
		
		return t.getValue();

	}

	public static void updateStationsData() throws Exception {

		String value = "";
		URL url = new URL("https://toronto.bixi.com/data/bikeStations.xml");
		URLConnection yc = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null)
			value = value + inputLine;
		in.close();

		key = KeyFactory.createKey("bikes", "bikes");

		Entity stations = new Entity("BikesData", key);
		Text valuetext = new Text(value);
		stations.setProperty("data", valuetext);

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		datastore.put(stations);
	}

}
