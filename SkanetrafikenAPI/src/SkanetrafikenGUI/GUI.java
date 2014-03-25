package SkanetrafikenGUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.EventQueue;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.VirtualEarthTileFactoryInfo;
import org.jdesktop.swingx.input.CenterMapListener;
import org.jdesktop.swingx.input.PanKeyListener;
import org.jdesktop.swingx.input.PanMouseInputListener;
import org.jdesktop.swingx.input.ZoomMouseWheelListenerCenter;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;

import SkanetrafikenGUI.FancyWaypointRenderer;
import SkanetrafikenGUI.MyWaypoint;
import se.mah.k3lara.skaneAPI.model.Journey;
import se.mah.k3lara.skaneAPI.model.Journeys;
import se.mah.k3lara.skaneAPI.model.Station;
import se.mah.k3lara.skaneAPI.view.Constants;
import se.mah.k3lara.skaneAPI.xmalparser.Parser;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.AbstractListModel;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	private Station station;
	private ArrayList<Station> searchStations = new ArrayList<Station>();
	private DefaultListModel<Object> listModel = new DefaultListModel<Object>();
	private JList<Object> list = new JList<Object>(listModel);
	private String selectedListItem;
	private List<Station> selectedStation;
	private JLabel lblNewLabel = new JLabel();


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JXMapViewer mapViewer = new JXMapViewer();
		mapViewer.setBounds(200, 0, 800, 600);
		
		contentPane.add(mapViewer);
		
		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		tileFactory.setThreadPoolSize(8);
		mapViewer.setTileFactory(tileFactory);
		
		GeoPosition malmoCentralen = new GeoPosition(55.609147, 12.999034);
		GeoPosition malmoNobeltorget = new GeoPosition(55.591298, 13.019252);

		// Create a track from the geo-positions
		List<GeoPosition> track = Arrays.asList(malmoCentralen, malmoNobeltorget);
		RoutePainter routePainter = new RoutePainter(track);

		// Set the focus
		mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);

		// Create waypoints from the geo-positions
		Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>(Arrays.asList(
				new MyWaypoint("A", Color.ORANGE, malmoCentralen),
				new MyWaypoint("B", Color.RED, malmoNobeltorget)));

		// Create a waypoint painter that takes all the waypoints
		WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
		waypointPainter.setWaypoints(waypoints);
		waypointPainter.setRenderer(new FancyWaypointRenderer());
		
		// Create a compound painter that uses both the route-painter and the waypoint-painter
		List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
		painters.add(routePainter);
		painters.add(waypointPainter);
		
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
		mapViewer.setOverlayPainter(painter);
		
		// Add interactions
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);
		mapViewer.addMouseListener(new CenterMapListener(mapViewer));
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
		mapViewer.addKeyListener(new PanKeyListener(mapViewer));
		
		textField = new JTextField();
		textField.setBounds(0, 0, 200, 30);
		textField.setBackground(Color.WHITE);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(0, 30, 200, 23);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				searchStations.addAll(Parser.getStationsFromURL(textField.getText()));
				for(Station s : searchStations){
					listModel.addElement(s.getStationName());
					System.out.println(s.getStationName());
				}
			}
		});
		contentPane.add(btnSearch);

		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setBounds(0, 54, 200, 507);
		contentPane.add(lblNewLabel);
		
		list.setBounds(0, 54, 200, 507);
		contentPane.add(list);
		
		list.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Create new positions before repaint
				GeoPosition malmoCentralen = new GeoPosition(55.609147, 12.999034);
				GeoPosition malmoUbatshallen = new GeoPosition(55.614969, 12.984621);

				// Create a track from the geo-positions
				List<GeoPosition> track = Arrays.asList(malmoCentralen, malmoUbatshallen);
				RoutePainter routePainter = new RoutePainter(track);

				// Set the focus
				mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);

				// Create waypoints from the geo-positions
				Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>(Arrays.asList(
						new MyWaypoint("A", Color.ORANGE, malmoCentralen),
						new MyWaypoint("B", Color.RED, malmoUbatshallen)));

				// Create a waypoint painter that takes all the waypoints
				WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
				waypointPainter.setWaypoints(waypoints);
				waypointPainter.setRenderer(new FancyWaypointRenderer());
				
				// Create a compound painter that uses both the route-painter and the waypoint-painter
				List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
				painters.add(routePainter);
				painters.add(waypointPainter);
				// Paint new route
				CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
				mapViewer.setOverlayPainter(painter);
				mapViewer.repaint();
				
				selectedListItem = list.getSelectedValue().toString();
				System.out.println(selectedListItem);
				selectedListItem = selectedListItem.replaceAll(" ", "");
				System.out.println(selectedListItem);
				selectedStation = Parser.getStationsFromURL(selectedListItem);
				System.out.println(selectedStation.get(0).getStationNbr());
				String searchURL = Constants.getURL("80000",selectedStation.get(0).getStationNbr(),1);
				Journeys journeys = Parser.getJourneys(searchURL);
				for (Journey journey : journeys.getJourneys()) {
					System.out.print(journey.getStartStation()+" - ");
					System.out.print(journey.getEndStation());
					lblNewLabel.setText(" Departs in "+journey.getTimeToDeparture()+ " minutes on bus " + journey.getLineOnFirstJourney() + " " + journey.getArrDateTime());
				}
				System.out.println(selectedStation.get(0).getLatitude() + " " + selectedStation.get(0).getLongitude());
				listModel.removeAllElements();
				list.removeAll();
			}
		}); 
	}
}
