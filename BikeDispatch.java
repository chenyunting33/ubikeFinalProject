import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class BikeDispatch extends JFrame implements ActionListener{
	private JComboBox<String> comboBox, comboBox2;
	JLabel mes, mesEmpty, mes3;
	JLabel bike;
	JButton move, cancel;
	
	BikeDispatch() {
		setLayout(null);
		setVisible(true);
		setSize(600, 420);
		this.setLocationRelativeTo(null);
		this.setTitle("Bike Dispatch");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		bike = new JLabel("select a dock");
		bike.setBounds(330,100,300,40);
		this.add(bike);
		
		move = new JButton("Move Bike");
		move.setBounds(330,200,100,40);
		move.addActionListener((ActionListener) this);
		this.add(move);
		
		mes = new JLabel("dock with bike");
		mes.setBounds(120,70,300,40);
		this.add(mes);
		
		comboBox = new JComboBox();
		comboBox.setBounds(100,100,200,40);
		this.add(comboBox);
		
		mesEmpty = new JLabel("empty dock");
		mesEmpty.setBounds(120,170,300,40);
		this.add(mesEmpty);
		
		comboBox2 = new JComboBox();
		comboBox2.setBounds(100,200,200,40);
		this.add(comboBox2);
		
		loadData();
		
		comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	showSelectedItem();
                }
            }
        });
		
		cancel = new JButton("Cancel/Back");
		cancel.setBounds(380,280,100,40);
		cancel.addActionListener(this);
		this.add(cancel);
		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==cancel) {
			dispose();
			new Staff();
		}
		if(e.getSource()==move) {
			String Dock = (String) comboBox.getSelectedItem();
			String emptyDock = (String) comboBox2.getSelectedItem();
			String bikemove = bike.getText();
			System.out.println(emptyDock);
			
			try {
				Connection con = DriverManager.getConnection
						("jdbc:mysql://localhost:3306/ubike","root","password");
				Statement stmt = con.createStatement();
				
				stmt.executeUpdate
				("UPDATE docks SET `Bike` = '"+ bikemove +"' WHERE `DockUID` = '"+ emptyDock +"';");
				
				 // 禁用安全更新模式
	            stmt.execute("SET SQL_SAFE_UPDATES = 0");
				
	            stmt.executeUpdate
				("UPDATE docks SET Bike = '' WHERE DockUID ='"+Dock+"';");
	
	            // 重新啟用安全更新模式（可選）
	            stmt.execute("SET SQL_SAFE_UPDATES = 1");
	            
	            bike.setText("The bike has been moved.");
				
	            stmt.close();
				con.close();		
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
	
	 private void showSelectedItem() {
		 try {
			String selectedItem = (String) comboBox.getSelectedItem();
		    String query = "SELECT Bike FROM ubike.docks WHERE DockUID = ?";
		    try (Connection con = DriverManager.getConnection
		    		("jdbc:mysql://localhost:3306/ubike", "root", "password");
		         PreparedStatement stmt = con.prepareStatement(query)) {
		        stmt.setString(1, selectedItem);
		        try (ResultSet rs = stmt.executeQuery()) {
		            if (rs.next()) {
		                String selectedBike = rs.getString("Bike");
		                bike.setText(selectedBike);
		            } else {
		                bike.setText("No bike found");
		            }
			        }
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			} 
	        //String selectedItem = (String) comboBox.getSelectedItem();
	        //bike.setText("Selected item: " + selectedItem);
	        
//	        try {
//    			Connection con = DriverManager.getConnection
//    					("jdbc:mysql://localhost:3306/ubike","root","password");
//    			Statement stmt = con.createStatement();
//    			ResultSet rs = stmt.executeQuery
//    					("select Bike from ubike.docks where DockUID =' " + selectedItem +"'");
//    			bike.setText("the bike: ");
//    			while (rs.next()){ 
//    				String selectedBike = rs.getString("Bike");
//    				System.out.println("Bike");
//    				bike.setText("the bike: "+selectedBike);
//    			}		
//    			rs.close();
//    			con.close();		
//    		} catch (Exception exception) {
//    			exception.printStackTrace();
//    		}
	 }
	
	private void loadData() {
		try {
			Connection con = DriverManager.getConnection
					("jdbc:mysql://localhost:3306/ubike","root","password");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from ubike.docks");
			
			while (rs.next()){  
				String bike = rs.getString("bike");
				if(bike.length()!=0) {
					String dock = rs.getString("DockUID");
					comboBox.addItem(dock);
				}
				else {
					String dock2 = rs.getString("DockUID");
					comboBox2.addItem(dock2);
				}
			}		
			rs.close();
			con.close();		
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
