package api_ircbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class IRCBot extends PircBot {
	public IRCBot(){
		this.setName("MapsWeatherBot");
	}
	
	public void onDisconnect() {
		while(!isConnected()) {
			try {
				reconnect();
			}
			catch(Exception e) {}
		}
	}
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (message.equalsIgnoreCase("time")) {
			String time = new java.util.Date().toString();
			sendMessage(channel, sender + ": The time is now " + time);
		}
		if (message.matches("[\\d]+[A-Za-z0-9\\s,\\.]+?[\\d\\-]+"));
			String[] coordinates = startGoogleMapsRequest(message);
			sendMessage(channel, Colors.BLUE + "According to Google Maps, the coordinates of " + Colors.BOLD + message
					  + Colors.NORMAL + Colors.BLUE + " are " + Colors.BOLD + coordinates[0] + " , " + coordinates[1] + ".");
			
			String weather = startWeatherRequest(coordinates);
			sendMessage(channel, Colors.BLUE + "According to Weather.com, the weather at those coordinates"
					  + " is "+ Colors.BOLD + weather + ".");

	}
	
	public void onJoin(String channel, String sender, String login, String hostname) {
			sendMessage(channel, Colors.BLUE + "Welcome, " + sender + ", I am a chatbot that can look up"
					+ "coordinates and the weather at those coordinates.");
			sendMessage(channel, Colors.BLUE + "Type in an address to begin. Try it! ");
	}
	
	static String[] startGoogleMapsRequest(String address) {
		String key = "AIzaSyBYoImdGotxd0YUesX74bgblXKXfu8BG3w";
		
		String mapsURL = "https://maps.googleapis.com/maps/api/geocode/"
					   + "json?address=" + address + "&key=" + key;
		mapsURL = mapsURL.replaceAll(" ", "%20");
		
		StringBuilder result = new StringBuilder();
		try {
			URL url = new URL(mapsURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
			String line;
			while ((line = read.readLine()) != null) {
				result.append(line);
			}
			read.close();
			
			//Search for latitude and longitude
			double lat = parseLatJson(result.toString());
			double lng = parseLngJson(result.toString());
			
			String coordinates[] = {Double.toString(lat), Double.toString(lng)};
			
			return coordinates;
		}
		catch (Exception e) { 
			String[] error = {"Error! Exception: " + e};
			return error;
		}
	}
	
	static double parseLatJson(String json) {
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray resultsArray = jobject.getAsJsonArray("results");
		
		JsonObject resultsContent = resultsArray.get(0).getAsJsonObject();
		JsonObject geometry = resultsContent.getAsJsonObject("geometry");
		JsonObject location = geometry.getAsJsonObject("location");
		double lat = location.get("lat").getAsDouble();
		
		return lat;
	}
	
	static double parseLngJson(String json) {
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray resultsArray = jobject.getAsJsonArray("results");
		
		JsonObject resultsContent = resultsArray.get(0).getAsJsonObject();
		JsonObject geometry = resultsContent.getAsJsonObject("geometry");
		JsonObject location = geometry.getAsJsonObject("location");
		double lng = location.get("lng").getAsDouble();
		
		return lng;

	}
	static String startWeatherRequest(String[] coordinates) {
		String key = "ef854a66ffbc36719b7ad0367cf7c52f";
		String weatherURL = "http://api.openweathermap.org/data/2.5/weather?"
				+ "lat="+ coordinates[0] +"&lon=" + coordinates[1] 
				+ "&appid=" + key;
		
		weatherURL = weatherURL.replaceAll(" ", "%20");
		
		StringBuilder result = new StringBuilder();
		try {
			URL url = new URL(weatherURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
			String line;
			while ((line = read.readLine()) != null) {
				result.append(line);
			}
			read.close();
			
			//Search for latitude and longitude
			String weather = parseDescription(result.toString());
			return weather;
		}
		catch (Exception e) { 
			return "Error! Exception: " + e;
		}
	}
	
	static String parseDescription (String json) {
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject root = jelement.getAsJsonObject();
		JsonArray weatherArray = root.getAsJsonArray("weather");
		
		JsonObject weatherContents = weatherArray.get(0).getAsJsonObject();
		String description = weatherContents.get("description").getAsString();
		
		return description;
	}
}


