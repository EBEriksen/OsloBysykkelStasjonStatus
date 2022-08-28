import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;

public class Station {
	
	private int station_id, capacity, num_bikes_available, num_docks_available;
	private String name, address;
	private HashMap<String,String> rental_uris = new HashMap<String, String>();
	private double lat, lon;
	private boolean is_installed, is_renting, is_returning;
	private long last_reported; 
	
	Station(JSONObject station, JSONObject status) {
		station_id = station.getInt("station_id");
		name = station.getString("name");
		address = station.getString("address");
		
		Iterator<?> keys = station.getJSONObject("rental_uris").keys();
		rental_uris.put((String) keys.next(), station.getJSONObject("rental_uris").getString("android"));
		rental_uris.put((String) keys.next(), station.getJSONObject("rental_uris").getString("ios"));
		
		lat = station.getDouble("lat");
		lon = station.getDouble("lon");
		capacity = station.getInt("capacity");
		
		is_installed = status.getInt("is_installed") == 1 ? true : false;
		is_renting = status.getInt("is_renting") == 1 ? true : false;
		is_returning = status.getInt("is_returning") == 1 ? true : false;
		last_reported = status.getLong("last_reported");
		num_bikes_available = status.getInt("num_bikes_available");
		num_docks_available = status.getInt("num_docks_available");
	}

	@Override
	public String toString() {
		return "Station [station_id=" + station_id + ", capacity=" + capacity + ", num_bikes_available="
				+ num_bikes_available + ", num_docks_available=" + num_docks_available + ", name=" + name + ", address="
				+ address + ", rental_uris=" + rental_uris + ", lat=" + lat + ", lon=" + lon + ", is_installed="
				+ is_installed + ", is_renting=" + is_renting + ", is_returning=" + is_returning + ", last_reported="
				+ last_reported + "]";
	}

	public int getStation_id() {
		return station_id;
	}

	public void setStation_id(int station_id) {
		this.station_id = station_id;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public HashMap<String, String> getRental_uris() {
		return rental_uris;
	}

	public void setRental_uris(HashMap<String, String> rental_uris) {
		this.rental_uris = rental_uris;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public boolean isIs_installed() {
		return is_installed;
	}

	public void setIs_installed(boolean is_installed) {
		this.is_installed = is_installed;
	}

	public boolean isIs_renting() {
		return is_renting;
	}

	public void setIs_renting(boolean is_renting) {
		this.is_renting = is_renting;
	}

	public boolean isIs_returning() {
		return is_returning;
	}

	public void setIs_returning(boolean is_returning) {
		this.is_returning = is_returning;
	}

	public int getNum_bikes_available() {
		return num_bikes_available;
	}

	public void setNum_bikes_available(int num_bikes_available) {
		this.num_bikes_available = num_bikes_available;
	}

	public int getNum_docks_available() {
		return num_docks_available;
	}

	public void setNum_docks_available(int num_docks_available) {
		this.num_docks_available = num_docks_available;
	}

	public long getLast_reported() {
		return last_reported;
	}
	
	public String getReadableLast_reported() {
		LocalDateTime dateTime = LocalDateTime.ofEpochSecond(last_reported, 0, ZoneOffset.UTC);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		String formattedDate = dateTime.format(formatter);
		return formattedDate;
	}

	public void setLast_reported(long last_reported) {
		this.last_reported = last_reported;
	}
}
