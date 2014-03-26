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
import java.awt.Font;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	private Station station;
	private ArrayList<Station> searchStations = new ArrayList<Station>();
	// Declares a list of objects to be used in the JList.
	private DefaultListModel<Object> listModel = new DefaultListModel<Object>();
	private JList<Object> list = new JList<Object>(listModel);
	private String selectedListItem;
	private List<Station> selectedStation;
	private JLabel lblNewLabel = new JLabel();
	private JLabel lblNewLabel2 = new JLabel();

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
		
		/***
		 * Create map
		 */
		final JXMapViewer mapViewer = new JXMapViewer();
		mapViewer.setBounds(200, 0, 800, 600);
		contentPane.add(mapViewer);
		
		/***
		 * Configure JXMapViewer 
		 */
		TileFactoryInfo info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		tileFactory.setThreadPoolSize(8);
		mapViewer.setTileFactory(tileFactory);
		/***
		 * Set start positions
		 */
		GeoPosition malmoCentralen = new GeoPosition(55.609147, 12.999034);
		GeoPosition malmoNobeltorget = new GeoPosition(55.591298, 13.019252);

		/***
		 * Create line between positions
		 */
		List<GeoPosition> track = Arrays.asList(malmoCentralen, malmoNobeltorget);
		RoutePainter routePainter = new RoutePainter(track);

		/***
		 * Adjust focus to fit the line
		 */
		mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);

		/***
		 * Create waypoints
		 */
		Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>(Arrays.asList(
				new MyWaypoint("A", Color.ORANGE, malmoCentralen),
				new MyWaypoint("B", Color.RED, malmoNobeltorget)));

		/***
		 * Send waypoints to painter
		 */
		WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
		waypointPainter.setWaypoints(waypoints);
		waypointPainter.setRenderer(new FancyWaypointRenderer());
		
		/***
		 * Combine painters for waypoints and line
		 */
		List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
		painters.add(routePainter);
		painters.add(waypointPainter);
		
		/***
		 * Paint overlays to map
		 */
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
		mapViewer.setOverlayPainter(painter);
		
		/***
		 * Add map interactions
		 */
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);
		mapViewer.addMouseListener(new CenterMapListener(mapViewer));
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
		mapViewer.addKeyListener(new PanKeyListener(mapViewer));
		
		/***
		 * Create textfield for search
		 */
		textField = new JTextField();
		textField.setBounds(0, 0, 200, 30);
		textField.setBackground(Color.WHITE);
		contentPane.add(textField);
		textField.setColumns(10);
		
		/***
		 * Add searchbutton and actionListener.
		 * Uses the textfield as an argument for station api.
		 * Adds each result to the listModel.
		 */
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(0, 30, 200, 23);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				searchStations.addAll(Parser.getStationsFromURL(textField.getText()));
				for(Station s : searchStations){
					listModel.addElement(s.getStationName());
				}
			}
		});
		contentPane.add(btnSearch);
		
		/***
		 * Creates labels for result from journey api.
		 */
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		lblNewLabel.setBounds(0, 54, 200, 23);
		contentPane.add(lblNewLabel);

		lblNewLabel2.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		lblNewLabel2.setBounds(0, 87, 200, 23);	
		contentPane.add(lblNewLabel2);
		
		/***
		 * Creates list for results from station api.
		 */
		list.setBounds(0, 122, 200, 439);
		contentPane.add(list);
		
		/***
		 * Creates an actionListener for list.
		 * Activates on selecting listItem.
		 */
		list.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()){
					/***
					 * Set new positions
		 			 */
					GeoPosition malmoCentralen = new GeoPosition(55.609147, 12.999034);
					GeoPosition malmoUbatshallen = new GeoPosition(55.614969, 12.984621);
	
					/***
					 * Create line between positions
		 			 */
					List<GeoPosition> track = Arrays.asList(malmoCentralen, malmoUbatshallen);
					RoutePainter routePainter = new RoutePainter(track);
	
					/***
					 * Adjust focus to fit the line
		 			 */
					mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);
	
					/***
					 * Create waypoints
		 			 */
					Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>(Arrays.asList(
							new MyWaypoint("A", Color.ORANGE, malmoCentralen),
							new MyWaypoint("B", Color.RED, malmoUbatshallen)));
					
					/***
					 * Send waypoints to painter
		 			 */
					WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
					waypointPainter.setWaypoints(waypoints);
					waypointPainter.setRenderer(new FancyWaypointRenderer());
					
					/***
					 * Combine painters for waypoints and line
		 			 */
					List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
					painters.add(routePainter);
					painters.add(waypointPainter);
					
					/***
					 * Paint overlays to map
		 			 */
					CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
					mapViewer.setOverlayPainter(painter);
					
					/***
					 * Repaint map
		 			 */
					mapViewer.repaint();
					
					/***
					 * Casts the selectedItem in list to a String.
					 * Removes whitespaces in the selectedListItem.
					 * Use selectedListItem as an argument for Parser api.
					 * Use selectedStation ID api and Malmö C ID as an argument for Constants api.
					 * Use searchURL as an argument for Journeys api.
					 * Uses journeys to fill the labels with information from the Journey.
					 */
					selectedListItem = (String) list.getSelectedValue();
					if(selectedListItem != null){
						selectedListItem = selectedListItem.replaceAll(" ", "");
						selectedStation = Parser.getStationsFromURL(selectedListItem);
						String searchURL = Constants.getURL("80000",selectedStation.get(0).getStationNbr(),1);
						Journeys journeys = Parser.getJourneys(searchURL);
						for (Journey journey : journeys.getJourneys()) {
							lblNewLabel.setText(" Departs in " + journey.getTimeToDeparture() + " min from " + journey.getStartStation());
							lblNewLabel2.setText(" Travel time is " + journey.getTravelMinutes() + " min");
						}
						//listModel.removeAllElements();
						//list.removeAll();
					}
				}
			}
		}); 
	}
}
