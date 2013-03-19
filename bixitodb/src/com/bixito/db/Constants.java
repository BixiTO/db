package com.bixito.db;

import com.google.appengine.api.datastore.Key;

public class Constants {

	public static final String STATS_SERVER = "http://bixito.andrewl.ca/data";
	public static String  BIXI_DATA_URL = "https://toronto.bixi.com/data/bikeStations.xml";
	public static String  BIXI_DATA_DB_KIND = "BikesData";
	public static String  BIXI_DATA_DB_PROPERTY = "data";
	public static Key BIXI_DATA_KEY = null;
	public static String DEVICE_ID = "deviceId";
}