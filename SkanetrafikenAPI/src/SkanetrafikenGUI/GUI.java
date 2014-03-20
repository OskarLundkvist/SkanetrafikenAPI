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
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	private Station station;
	ArrayList<Station> searchStations = new ArrayList<Station>();

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
			}
		});
		contentPane.add(btnSearch);
		
		JList list = new JList<Object>();
		list.setModel(new AbstractListModel<Object>() {
			public int getSize() {
				return searchStations.size();
			}
			public Object getElementAt(int index) {
				return searchStations.get(index);
			}
		});
		list.setBounds(0, 52, 200, 509);
		contentPane.add(list);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBackground(Color.BLACK);
		separator.setForeground(Color.BLACK);
		separator.setBounds(200, 0, 1, 561);
		contentPane.add(separator);
		
	}
}
