package SkanetrafikenGUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;

import se.mah.k3lara.skaneAPI.model.Station;
import se.mah.k3lara.skaneAPI.xmalparser.Parser;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.AbstractListModel;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	private String[] stations = new String[2];
	private Station station;

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
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(200, 0, 600, 600);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(0, 0, 200, 61);
		textField.setBackground(Color.WHITE);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(10, 72, 182, 23);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<Station> searchStations = new ArrayList<Station>(); 
				searchStations.addAll(Parser.getStationsFromURL(textField.getText()));
				for (int c = 0; c <= 2; c++){
					stations[c] = new String (station.getStationName());
				}
				System.out.println(stations[0]);
				System.out.println(stations[1]);
				System.out.println(stations[2]);
			}
		});
		contentPane.add(btnSearch);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(0, 0, 200, 600);
		textArea.setBackground(UIManager.getColor("DesktopIcon.borderRimColor"));
		contentPane.add(textArea);
		
		JList list = new JList();
		list.setModel(new AbstractListModel() {
			public int getSize() {
				return stations.length;
			}
			public Object getElementAt(int index) {
				return stations[index];
			}
		});
		list.setBounds(0, 93, 200, 307);
		contentPane.add(list);
		
	}
}
