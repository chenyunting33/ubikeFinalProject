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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class RentBike extends JFrame implements ActionListener{
	private JComboBox<String> station, card;
	JLabel chooseSta, chooseCard, timeText;
	JButton rent, cancel;
	JTextField time;
	
	RentBike() {
		setLayout(null);
		setVisible(true);
		setSize(600, 420);
		this.setLocationRelativeTo(null);
		this.setTitle("Rent Bike");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		chooseSta = new JLabel("select a station");
		chooseSta.setBounds(70,70,300,40);
		this.add(chooseSta);
		
		station = new JComboBox();
		station.setBounds(70,100,200,40);
		this.add(station);
		
		loadData();
		
		chooseCard = new JLabel("select your card");
		chooseCard.setBounds(300,70,300,40);
		this.add(chooseCard);
		
		card = new JComboBox();
		card.setBounds(300,100,150,40);
		this.add(card);
		
		loadCardData();
		
		card.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                	showSelectedItem();
                }
            }
        });
		
		timeText = new JLabel("enter the time (YYYY-MM-DD hh:mm:ss)");
		timeText.setBounds(70,150,300,40);
		this.add(timeText);
		
		time = new JTextField();
		time.setBounds(70,180,200,40);
		this.add(time);
		
		rent = new JButton("Rent");
		rent.setBounds(380,180,100,40);
		rent.addActionListener((ActionListener) this);
		this.add(rent);
		
		cancel = new JButton("Cancel/Back");
		cancel.setBounds(380,230,100,40);
		cancel.addActionListener(this);
		this.add(cancel);
		
	}

	//load station data
	private void loadData() {
		try {
			Connection con = DriverManager.getConnection
					("jdbc:mysql://localhost:3306/ubike","root","password");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select StationUID,Bike from ubike.docks");
			
			String stationid = "0";
			while (rs.next()){  	
				String bike = rs.getString("bike");
				if(bike.length()!=0) {
					if(!stationid.equals(rs.getString("StationUID"))) {
						stationid = rs.getString("StationUID");
						station.addItem(stationid);
					}
				}
			}		
			con.close();		
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private void loadCardData() {
		try {
			Connection con = DriverManager.getConnection
					("jdbc:mysql://localhost:3306/ubike","root","password");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery
					("select * from ubike.cards where userid = "+DatabaseHelper.signinData.userid);
			
			while (rs.next()){  	
				card.addItem(rs.getString("cardid"));	
			}
			rs.close();
			con.close();		
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	 private void showSelectedItem() {
		 try {
			String selectedItem = (String) card.getSelectedItem();
		    String query = "SELECT * FROM ubike.cards WHERE cardid = ?";
		    try (Connection con = DriverManager.getConnection
		    		("jdbc:mysql://localhost:3306/ubike", "root", "password");
		         PreparedStatement stmt = con.prepareStatement(query)) {
		        stmt.setString(1, selectedItem);
		        try (ResultSet rs = stmt.executeQuery()) {
		            if (rs.next()) {
		                String cardbalance = rs.getString("balance");
		                if(cardbalance.equals("0")) {
		                	JOptionPane.showMessageDialog(this, "The balance of "+selectedItem+" is ZERO. Please top up.");
		        			new Recharge();
		        			dispose();
						}else {
			                chooseCard.setText("balance: "+cardbalance);
						}
		            } else {
		                chooseCard.setText("No card found");
		            }
			        }
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			} 
	 }
	
	 
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==cancel) {
			new MyFrame();
			dispose();
		}
		if(e.getSource()==rent) {
			String dateTimeStr = time.getText();
            try {
                // Convert the input string to Timestamp
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedDate = dateFormat.parse(dateTimeStr);
                Timestamp timestamp = new Timestamp(parsedDate.getTime());

                // Insert the timestamp into the database
                insertDateTimeIntoDatabase(timestamp);
                DatabaseHelper.signinData.setRentTime(dateTimeStr);
            } catch (ParseException ex) {
            	ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid date format.");
            }
		}
	}
	
	private void insertDateTimeIntoDatabase(Timestamp timestamp) {
        String url = "jdbc:mysql://localhost:3306/ubike";
        String user = "root";
        String password = "password";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            

            String bikerented = null;
            
            try {			    
			    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM ubike.docks where stationUID='"+station.getSelectedItem()+"';");
				
				while (rs.next()){  
					String tempBike = rs.getString("bike");
					if(tempBike.length()!=0) {
						bikerented = tempBike;
						String dockUID = rs.getString("dockUID");
						
						// 禁用安全更新模式
			            stmt.execute("SET SQL_SAFE_UPDATES = 0");
						
			            stmt.executeUpdate
						("UPDATE docks SET Bike = '' WHERE DockUID ='"+dockUID+"';");
			
			            // 重新啟用安全更新模式（可選）
			            stmt.execute("SET SQL_SAFE_UPDATES = 1");
					}
				}
			} catch (SQLException evt) {
			    evt.printStackTrace();
			}
            
            conn = DriverManager.getConnection(url, user, password);

            String sql = "INSERT INTO records (userid, cardid, BikeUID, RentStationUID, RentTime) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            // Set parameters
            pstmt.setInt(1,Integer.parseInt(DatabaseHelper.signinData.userid)); // Example user ID
            pstmt.setString(2, (String) card.getSelectedItem()); // Example card ID
            pstmt.setString(3, bikerented); // Example bike ID
            pstmt.setString(4, (String) station.getSelectedItem()); // Example rent station ID
            pstmt.setTimestamp(5, timestamp); // The user input timestamp
          
            // Execute insert operation
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "You have rent a bike!");
                DatabaseHelper.signinData.setRent();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error accessing database.");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}