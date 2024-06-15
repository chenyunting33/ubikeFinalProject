import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BikeCross extends JFrame implements ActionListener{
	JLabel authroity,mes;
	private JComboBox<String> authBox;
	private HashSet<String> comboBoxItems = new HashSet<>();
	JButton search, cancel;
	private JTable table;
	private DefaultTableModel tableModel;
	
	BikeCross() {
		setLayout(null);
		setVisible(true);
		setSize(400, 600);
		this.setLocationRelativeTo(null);
		this.setTitle("Bike Cross");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		authroity = new JLabel("select the authroity");
		authroity.setBounds(70,20,200,40);
		this.add(authroity);
		
		authBox = new JComboBox();
		authBox.setBounds(70,50,100,40);
		this.add(authBox);
		
		loadData();
		
		search = new JButton("search");
		search.setBounds(230,50,100,40);
		search.addActionListener(this);
		this.add(search);
		
		  // 初始化表格
        String[] columnNames = {"AuthorityUID", "BikeUID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBounds(70, 120, 250, 300);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        this.add(tablePanel);
		
		cancel = new JButton("Cancel/Back");
		cancel.setBounds(230,450,100,40);
		cancel.addActionListener(this);
		this.add(cancel);
	}
	
	private void loadData() {
		try {
			Connection con = DriverManager.getConnection
					("jdbc:mysql://localhost:3306/ubike","root","password");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select AuthorityID from ubike.bikes");
			
			while (rs.next()){ 
				String authID = rs.getString("AuthorityID");
				if(!comboBoxItems.contains(authID)) {
					authBox.addItem(authID);
		            comboBoxItems.add(authID);
				}
			}		
			rs.close();
			con.close();		
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==cancel) {
			dispose();
			new Staff();
		}
		if(e.getSource()==search) {
			tableModel.setRowCount(0);
			try {
				String selectedItem = (String) authBox.getSelectedItem();
			    
			    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM ubike.bikes");
				
				while (rs.next()){  
					String bikeAuth = rs.getString("AuthorityID");
					if(!bikeAuth.equals(selectedItem)) {
			             String bikeCross = rs.getString("BikeUID");
			             tableModel.addRow(new Object[]{bikeAuth, bikeCross});
					}
				}
			} catch (SQLException evt) {
			    evt.printStackTrace();
			}
		}
		
	}
	
//	private void showSelectedItem() {
//		 try {
//			String selectedItem = (String) authBox.getSelectedItem();
//		    String query = "SELECT AuthorityID FROM ubike.bikes WHERE AuthorityID = ?";
//		    try (Connection con = DriverManager.getConnection
//		    		("jdbc:mysql://localhost:3306/ubike", "root", "password");
//		         PreparedStatement stmt = con.prepareStatement(query)) {
//		        stmt.setString(1, selectedItem);
//		        try (ResultSet rs = stmt.executeQuery()) {
//		            if (rs.next()) {
//		                String selectedBike = rs.getString("Bike");
//		                bike.setText(selectedBike);
//		            } else {
//		                bike.setText("No bike found");
//		            }
//			        }
//			    }
//			} catch (SQLException e) {
//			    e.printStackTrace();
//			}
//	}
}
