import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

public class Main {
	
	private static JFrame frame;
	private static JPanel panel;
	private static JScrollPane scrollableList;
	private static ArrayList<Station> mergedList;
	private static JList<Object> list;
	private static JTextPane pane;
	private static ImageIcon img = new ImageIcon("./src/icon.png");
	private static JXMapViewer mapViewer;
	private static int zoomLevel;
	private static JFrame mapFrame = new JFrame("Oslo");

	public static void main(String[] args) throws IOException, InterruptedException {
		JSONObject stationObj = readJsonFromUrl("https://gbfs.urbansharing.com/oslobysykkel.no/station_information.json");
		JSONObject stationStateObj = readJsonFromUrl("https://gbfs.urbansharing.com/oslobysykkel.no/station_status.json");
		
		if (stationObj != null && stationStateObj != null) {						
			mergeLists(stationObj, stationStateObj);
			fillList(mergedList);
			
			makeListFrame();
			makeMap();
		}
	}
	
	public static void makeListFrame() {
		frame = new JFrame();
		
	    scrollableList = new JScrollPane(list);
	    		    
	    pane = new JTextPane();
	    pane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
	    pane.setEditable(false);
	    
	    scrollableList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setLayout(new GridLayout(0, 2));
		panel.add(scrollableList);
		panel.add(pane);
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Oslo Bysykkel Stasjoner");
		frame.setSize(500, 400);
		frame.setIconImage(img.getImage());
		frame.setLocation(1200, 300);
		frame.setVisible(true);
	}
	
	public static void makeMap() {
		mapViewer = new JXMapViewer();
		zoomLevel = 7;

        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        tileFactory.setThreadPoolSize(8);

        GeoPosition oslo = new GeoPosition(59.9238688, 10.74224539);

        mapViewer.setZoom(zoomLevel);
        mapViewer.setAddressLocation(oslo);
        MouseInputListener mouseInputListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mouseInputListener);
        mapViewer.addMouseMotionListener(mouseInputListener);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        
        makeWaypoints();
        
        mapFrame = new JFrame("Oslo");
        mapFrame.getContentPane().add(mapViewer);
        mapFrame.setSize(800, 600);
        mapFrame.setIconImage(img.getImage());
        mapFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mapFrame.setLocation(300, 200);
        mapFrame.setVisible(true);
	}
	
	public static void makeWaypoints() {
		ArrayList<DefaultWaypoint> waypointList = new ArrayList<DefaultWaypoint>();
		
		for (int i = 0; i < mergedList.size(); i++) {
			waypointList.add(new DefaultWaypoint(new GeoPosition(mergedList.get(i).getLat(), mergedList.get(i).getLon())));
		}

        Set<Waypoint> waypoints = new HashSet<Waypoint>(waypointList);

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypointPainter.setWaypoints(waypoints);

        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(waypointPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        mapViewer.setOverlayPainter(painter);
	}
	
	public static JSONObject readJsonFromUrl(String inputUrl) throws IOException {
		URL url = new URL(inputUrl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		
		if(conn.getResponseCode() == 200) {
			Scanner scan = new Scanner(url.openStream());
			String jsonText = "";
			while(scan.hasNext()) {
				String temp = scan.next();
				jsonText += temp;
            }
			JSONObject jsonObject = new JSONObject(jsonText);
			scan.close();
			
			return jsonObject;
        }
		
		return null;
	}
	
	public static void updatePanel(Station station) {
		String newPaneContent = "";
		newPaneContent += "Navn: " + station.getName()
		+ "\nId: " + station.getStation_id() 
		+ "\nAddresse: " + station.getAddress() 
		+ "\nKapasitet: " + station.getCapacity()
		+ "\n\nLedige Sykler: " + station.getNum_bikes_available() 
		+ "\nLedige Plasser: " + station.getNum_docks_available()
		+ "\n\nBreddegrad: " + (Math.round(station.getLat()*10000.0)/10000.0) 
		+ "\nLengdegrad: " + (Math.round(station.getLon()*10000.0)/10000.0) 
		+ "\n\nLeie URI:" 
		+ "\n   Android: " + station.getRental_uris().get("android") 
		+ "\n   Ios: " + station.getRental_uris().get("ios") 
		+ "\n\nSiste Rapport: " + station.getReadableLast_reported();
		pane.setText(newPaneContent);
	}
	
	public static void mergeLists(JSONObject obj, JSONObject obj2) {
		mergedList = new ArrayList<Station>();
		JSONArray trimmedStation = trimJSON(obj);
		JSONArray trimmedStatus = trimJSON(obj2);
		
		if (trimmedStation.length() == trimmedStatus.length()) {
			for (int i = 0; i < trimmedStation.length(); i++) {
				mergedList.add(new Station(trimmedStation.getJSONObject(i), trimmedStatus.getJSONObject(i)));
			}
		}		
	}
	
	public static JSONArray trimJSON(JSONObject obj) {
		JSONArray trimmed = obj.getJSONObject("data").getJSONArray("stations");
		
		return trimmed;
	}
	
	public static void fillList(ArrayList<Station> listContent) {
		ArrayList<String> temp = new ArrayList<String>();
		
		for (int i = 0; listContent.size() > i; i++) {
			temp.add(listContent.get(i).getName());
		}
		
		list = new JList<Object>(temp.toArray());
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = list.getSelectedIndex();
				updatePanel(mergedList.get(index));
			}
		});
	}
}