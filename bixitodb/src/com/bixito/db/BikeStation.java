package com.bixito.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.mortbay.log.Log;

public class BikeStation {
	int stationId;
	String stationName;
	String terminalName;
	long lastCommWithServer;
	double latitude;
	double longitude;
	boolean installed;
	boolean locked;
	boolean temporary;
	boolean publicStation;
	int nbBikes;
	int nbEmptyDocks;
	long latestUpdateTime;
	int popularity;

	/**
	 * 
	 * @param stationID
	 * @param stationName
	 * @param terminalName
	 * @param lastCommWithServer
	 * @param latitude
	 * @param longitude
	 * @param installed
	 * @param locked
	 * @param temporary
	 * @param publicStation
	 * @param nbBikes
	 * @param nbEmptyDocks
	 * @param latestUpdateTime
	 */
	public BikeStation(int stationId, String stationName, String terminalName,
			long lastCommWithServer, double latitude, double longitude,
			boolean installed, boolean locked, boolean temporary,
			boolean publicStation, int nbBikes, int nbEmptyDocks,
			long latestUpdateTime) {
		setStationId(stationId);
		setStationName(stationName);
		setTerminalName(terminalName);
		setLastCommWithServer(lastCommWithServer);
		setLatitude(latitude);
		setLongitude(longitude);
		setInstalled(installed);
		setLocked(locked);
		setTemporary(temporary);
		setPublicStation(publicStation);
		setNbBikes(nbBikes);
		setNbEmptyDocks(nbEmptyDocks);
		setLatestUpdateTime(latestUpdateTime);
		popularity = 0;
	}

	public BikeStation copy() {
		return new BikeStation(stationId, stationName, terminalName,
				lastCommWithServer, latitude, longitude, installed, locked,
				temporary, publicStation, nbBikes, nbEmptyDocks,
				latestUpdateTime);
	}

	public BikeStation() {
	}

	/**
	 * @return the stationId
	 */
	public int getStationId() {
		return stationId;
	}

	/**
	 * @param stationId
	 *            the stationId to set
	 */
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	/**
	 * @return the stationName
	 */
	public String getStationName() {
		return stationName;
	}

	/**
	 * @param stationName
	 *            the stationName to set
	 */
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	/**
	 * @return the terminalName
	 */
	public String getTerminalName() {
		return terminalName;
	}

	/**
	 * @param terminalName
	 *            the terminalName to set
	 */
	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	/**
	 * @return the lastCommWithServer
	 */
	public long getLastCommWithServer() {
		return lastCommWithServer;
	}

	/**
	 * @param lastCommWithServer
	 *            the lastCommWithServer to set
	 */
	public void setLastCommWithServer(long lastCommWithServer) {
		this.lastCommWithServer = lastCommWithServer;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the installed
	 */
	public boolean isInstalled() {
		return installed;
	}

	/**
	 * @param installed
	 *            the installed to set
	 */
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked
	 *            the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the temporary
	 */
	public boolean isTemporary() {
		return temporary;
	}

	/**
	 * @param temporary
	 *            the temporary to set
	 */
	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}

	/**
	 * @return the publicStation
	 */
	public boolean isPublicStation() {
		return publicStation;
	}

	/**
	 * @param publicStation
	 *            the publicStation to set
	 */
	public void setPublicStation(boolean publicStation) {
		this.publicStation = publicStation;
	}

	/**
	 * @return the nbBikes
	 */
	public int getNbBikes() {
		return nbBikes;
	}

	/**
	 * @param nbBikes
	 *            the nbBikes to set
	 */
	public void setNbBikes(int nbBikes) {
		this.nbBikes = nbBikes;
	}

	/**
	 * @return the nbEmptyDocks
	 */
	public int getNbEmptyDocks() {
		return nbEmptyDocks;
	}

	/**
	 * @param nbEmptyDocks
	 *            the nbEmptyDocks to set
	 */
	public void setNbEmptyDocks(int nbEmptyDocks) {
		this.nbEmptyDocks = nbEmptyDocks;
	}

	/**
	 * @return the latestUpdateTime
	 */
	public long getLatestUpdateTime() {
		return latestUpdateTime;
	}

	/**
	 * @param latestUpdateTime
	 *            the latestUpdateTime to set
	 */
	public void setLatestUpdateTime(long latestUpdateTime) {
		this.latestUpdateTime = latestUpdateTime;
	}

	public int getStationPopularity() throws Exception {

		
		if(this.popularity != 0) {
			return this.popularity;
		}
		DB.Log.warning("getting popularity of station "+stationId);
		HttpURLConnection ret = HttpUtility
				.sendGetRequest(Constants.STATS_STATIONS_SERVER + "/"
						+ stationId);
		if (ret.getResponseCode() == 500) {
			int popularity = createNewStationInDb();
			return popularity;
		}
		if (ret.getResponseCode() == 200) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(InputStream) ret.getContent()));
			String l = new String();
			while ((l = br.readLine()) != null) {
				if(l.contains("Rank:")){
					String s = br.readLine().substring(2);
					this.popularity = Integer.valueOf(s);
					ret.disconnect();
					return popularity;
				}
			}
		}
		ret.disconnect();
		return Integer.valueOf("50");
	}

	private int createNewStationInDb() throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("station[stationId]", String.valueOf(getStationId()));
		params.put("station[rank]", String.valueOf((int) (getStationId())));
		params.put("station[upordown]", String.valueOf(0));
		HttpURLConnection rsp = HttpUtility.sendPostRequest(
				Constants.STATS_STATIONS_SERVER, params);
		int code = rsp.getResponseCode();
		if (code == 200) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(InputStream) rsp.getContent()));
			String l = new String();
			while ((l = br.readLine()) != null) {
				if(l.contains("Rank:")){
					return Integer.valueOf(br.readLine().substring(2));
				}
			}
		}
		return 50;
	}

	public void setStationPopularity(int rank) throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		int upordown = 0;
		int prev = getStationPopularity();
		if (rank < prev) {
			upordown = prev - rank;
		} else if (rank == prev) {
			upordown = 0;
		} else {
			upordown = prev - rank;
		}
		
		params.put("station[stationId]", String.valueOf(stationId));
		params.put("station[rank]", String.valueOf(rank));
		params.put("station[upordown]", String.valueOf(upordown));
		
		DB.Log.warning("Setting rank new :"+rank+" old:"+prev+" stationId:"+this.getStationId());
//		HttpURLConnection r = HttpUtility.sendPutRequest(Constants.STATS_STATIONS_SERVER + "/"
//				+ stationId, params);
//		int code =	r.getResponseCode();
//		r.disconnect();
		URL url = new URL(Constants.STATS_STATIONS_SERVER + "/"+ stationId);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("DELETE");
		int responseCode = connection.getResponseCode();
		HttpURLConnection r = HttpUtility.sendPostRequest(Constants.STATS_STATIONS_SERVER, params);
		r.getResponseCode();

	}

	public void setLocalPopularity(int p) {
		this.popularity = p;
	}

}
