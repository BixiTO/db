package com.bixito.db;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.File;

public class StationParser {
	String sourceDocument;
	// Tags from the XML file
	static final String ROOT_TAG = "stations";
	// Currently unused, should give the last date/time of when the source
	// XML file was updated
	// static final String LIST_UPDATE_TIME = "lastUpdate";
	static final String STATION_TAG = "station";
	static final String ID = "id";
	static final String NAME = "name";
	static final String TERMINAL_NAME = "terminalName";
	static final String LAST_COMM_WITH_SERVER = "lastCommWithServer";
	static final String LATITUDE = "lat";
	static final String LONGITUDE = "long";
	static final String INSTALLED = "installed";
	static final String LOCKED = "locked";
	// static final String INSTALL_DATE = "installDate";
	// static final String REMOVAL_DATE = "removalDate";
	static final String TEMPORARY = "temporary";
	static final String PUBLIC = "public";
	static final String NUMBER_OF_BIKES = "nbBikes";
	static final String NUMBER_OF_EMPTY_DOCKS = "nbEmptyDocks";
	static final String STATION_LAST_UPDATE_TIME = "latestUpdateTime";

	public StationParser(String sourceDocument) throws MalformedURLException {
		this.sourceDocument = sourceDocument;
	}

	/**
	 * Returns the fully parsed bike station list
	 * 
	 * @return a BikeStation ArrayList
	 * @throws Exception
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<BikeStation> getStationList() throws Exception {
		List<BikeStation> stations = new ArrayList<BikeStation>();

		// //////////////////////////////////

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(sourceDocument);

		// optional, but recommended
		// read this -
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		System.out.println("Root element :"
				+ doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName(STATION_TAG);

		System.out.println("----------------------------");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			System.out.println("\nCurrent Element :" + nNode.getNodeName());

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				System.out.println("Staff id : " + eElement.getAttribute("id"));
				System.out.println("First Name : "
						+ eElement.getElementsByTagName("firstname").item(0)
								.getTextContent());
				System.out.println("Last Name : "
						+ eElement.getElementsByTagName("lastname").item(0)
								.getTextContent());
				System.out.println("Nick Name : "
						+ eElement.getElementsByTagName("nickname").item(0)
								.getTextContent());
				System.out.println("Salary : "
						+ eElement.getElementsByTagName("salary").item(0)
								.getTextContent());

			}

			// /////////////////////////////////

		}
		return stations;
	}

	public static List<BikeStation> parseStations(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = builder.parse(is);
		doc.getDocumentElement().normalize();

		// nList is the list of station nodes
		NodeList nList = doc.getChildNodes().item(0).getChildNodes();
		nList.item(0);
		nList.item(1);
		List<BikeStation> stations = new ArrayList<BikeStation>();

		// go through all the stations
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			NodeList children = nNode.getChildNodes();

			if (true) {

				stations.add(new BikeStation((int) (Integer
						.valueOf(getNodeValue(ID, children))), getNodeValue(
						NAME, children), getNodeValue(TERMINAL_NAME, children),
						0L, (double) (Double.valueOf(getNodeValue(LATITUDE,
								children))), (double) (Double
								.valueOf(getNodeValue(LONGITUDE, children))),
						(boolean) (Boolean.valueOf(getNodeValue(INSTALLED,
								children))), (boolean) (Boolean
								.valueOf(getNodeValue(LOCKED, children))),
						(boolean) (Boolean.valueOf(getNodeValue(TEMPORARY,
								children))), (boolean) (Boolean
								.valueOf(getNodeValue(PUBLIC, children))),
						(int) (Integer.valueOf(getNodeValue(NUMBER_OF_BIKES,
								children))), (int) (Integer
								.valueOf(getNodeValue(NUMBER_OF_EMPTY_DOCKS,
										children))), 0L));
			}
		}

		return stations;
	}

	private static String getNodeValue(String tagName, NodeList nodes) {
		for (int x = 0; x < nodes.getLength(); x++) {
			Node node = nodes.item(x);
			if (node.getNodeName().equalsIgnoreCase(tagName)) {
				NodeList childNodes = node.getChildNodes();
				for (int y = 0; y < childNodes.getLength(); y++) {
					Node data = childNodes.item(y);
					if (data.getNodeType() == Node.TEXT_NODE)
						return data.getNodeValue();
				}
			}
		}
		return "";
	}

}