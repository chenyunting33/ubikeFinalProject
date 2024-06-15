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
import java.time.Duration;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ReturnBike extends JFrame implements ActionListener{
	private JComboBox<String> station, card;
	JLabel chooseSta, chooseCard, timeText;
	JButton returnbike, cancel;
	JTextField time;
	
	ReturnBike() {
		setLayout(null);
		setVisible(true);
		setSize(600, 420);
		this.setLocationRelativeTo(null);
		this.setTitle("Return Bike");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		chooseSta = new JLabel("select the return station");
		chooseSta.setBounds(70,70,300,40);
		this.add(chooseSta);
		
		station = new JComboBox();
		station.setBounds(70,100,200,40);
		this.add(station);
		
		loadData();
		
//		chooseCard = new JLabel("select your card");
//		chooseCard.setBounds(300,70,300,40);
//		this.add(chooseCard);
//		
//		card = new JComboBox();
//		card.setBounds(300,100,150,40);
//		this.add(card);
//		
//		loadCardData();
//		
//		card.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (e.getStateChange() == ItemEvent.SELECTED) {
//                	showSelectedItem();
//                }
//            }
//        });
		
		timeText = new JLabel("enter return time (YYYY-MM-DD hh:mm:ss)");
		timeText.setBounds(70,150,300,40);
		this.add(timeText);
		
		time = new JTextField();
		time.setBounds(70,180,200,40);
		this.add(time);
		
		returnbike = new JButton("Return");
		returnbike.setBounds(380,180,100,40);
		returnbike.addActionListener((ActionListener) this);
		this.add(returnbike);
		
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
				if(bike.length()==0) {
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
	
//	private void loadCardData() {
//		try {
//			Connection con = DriverManager.getConnection
//					("jdbc:mysql://localhost:3306/ubike","root","password");
//			Statement stmt = con.createStatement();
//			ResultSet rs = stmt.executeQuery
//					("select * from ubike.cards where userid = "+DatabaseHelper.signinData.userid);
//			
//			while (rs.next()){  	
//				card.addItem(rs.getString("cardid"));	
//			}
//			rs.close();
//			con.close();		
//		} catch (Exception exception) {
//			exception.printStackTrace();
//		}
//	}
//	
//	 private void showSelectedItem() {
//		 try {
//			String selectedItem = (String) card.getSelectedItem();
//		    String query = "SELECT * FROM ubike.cards WHERE cardid = ?";
//		    try (Connection con = DriverManager.getConnection
//		    		("jdbc:mysql://localhost:3306/ubike", "root", "password");
//		         PreparedStatement stmt = con.prepareStatement(query)) {
//		        stmt.setString(1, selectedItem);
//		        try (ResultSet rs = stmt.executeQuery()) {
//		            if (rs.next()) {
//		                String cardbalance = rs.getString("balance");
//		                if(cardbalance.equals("0")) {
//		                	JOptionPane.showMessageDialog(this, "The balance of "+selectedItem+" is ZERO. Please top up.");
//		        			new Recharge();
//		        			dispose();
//						}else {
//			                chooseCard.setText("balance: "+cardbalance);
//						}
//		            } else {
//		                chooseCard.setText("No card found");
//		            }
//			        }
//			    }
//			} catch (SQLException e) {
//			    e.printStackTrace();
//			} 
//	 }
	
	 
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==cancel) {
			new MyFrame();
			dispose();
		}
		if(e.getSource()==returnbike) {
			String returnTimeStr = time.getText();
			String rentTimestr = DatabaseHelper.signinData.rentTime;
            try {
                // Convert the input string to Timestamp
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date returnParsedDate = dateFormat.parse(returnTimeStr);
                Timestamp returnTimestamp = new Timestamp(returnParsedDate.getTime());
                
                Date rentParsedDate = dateFormat.parse(rentTimestr);
                Timestamp rentTimestamp = new Timestamp(rentParsedDate.getTime());
                
                // Calculate the duration between the two times
                long milliseconds = returnTimestamp.getTime() - rentTimestamp.getTime();
                Duration duration = Duration.ofMillis(milliseconds);

                // Extract hours, minutes, and seconds from the duration
                long hours = duration.toHours();
                long minutes = duration.toMinutes() % 60;
                long seconds = duration.getSeconds() % 60;

                int cost=0;
                
                if(hours<1) {
                	if(minutes<30) cost=0;
                	else cost=10;
                }else if(hours<4) {
                	cost+=hours*20;
                	if(minutes<30) cost+=10;
                	else cost+=20;
                }
            	else if(hours<8) {
                	cost+=hours*40;
                	if(minutes<30) cost+=20;
                	else cost+=40;
                }else {
                	cost+=hours*80;
                	if(minutes<30) cost+=40;
                	else cost+=80;
                }

                // Insert the timestamp into the database
                insertDateTimeIntoDatabase(returnTimestamp,cost);
                DatabaseHelper.signinData.setRentTime("2024-06-15 05:00:00");
            } catch (ParseException ex) {
            	ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid date format.");
            }
		}
	}
	
	private void insertDateTimeIntoDatabase(Timestamp timestamp, int cost) {
        String url = "jdbc:mysql://localhost:3306/ubike";
        String user = "root";
        String password = "password";

        Connection conn = null;
        PreparedStatement pstmt = null;

        
        
        try {

        	 conn = DriverManager.getConnection(url, user, password);
        	 
        	String bike = "0";
        	String cardid = "0";
            
            Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ubike.records where userid='"+DatabaseHelper.signinData.userid+"';");
            
			while(rs.next()) {
				String temp = rs.getString("returnStationUID");
				if(temp==null) {
					bike = rs.getString("bikeUID");
					cardid = rs.getString("cardid");
				}
			}
			            
            insertBike(bike);
        	
           

            String sql = "UPDATE records SET ReturnStationUID = ?, ReturnTime = ? , cost = ? WHERE cost IS NULL";
            pstmt = conn.prepareStatement(sql);
            
            // Set parameters
            pstmt.setString(1,(String) station.getSelectedItem()); // Example return station ID
            pstmt.setTimestamp(2, timestamp); // The user input timestamp
            pstmt.setString(3, Integer.toString(cost)); 
            
			costCard(cost,cardid);
          
            // Execute insert operation
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Your bike has been returned!");
                DatabaseHelper.signinData.setReturn();
            }
            
       
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error accessing database.");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
				System.out.println("?");
            	ex.printStackTrace();
            }
        }
    }
	
	public void insertBike(String bikerented) {
		
        try {			    
		    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ubike.docks where stationUID='"+station.getSelectedItem()+"';");
			
			while (rs.next()){  
				String tempBike = rs.getString("Bike");
				if(tempBike.length()==0) {
					
					String dockUID = rs.getString("dockUID");
					
					// 禁用安全更新模式
		            stmt.execute("SET SQL_SAFE_UPDATES = 0");
					
		            stmt.executeUpdate
					("UPDATE docks SET Bike = '"+bikerented+"' WHERE DockUID ='"+dockUID+"';");
		
		            // 重新啟用安全更新模式（可選）
		            stmt.execute("SET SQL_SAFE_UPDATES = 1");
				}
			}
		} catch (SQLException evt) {
			System.out.println("?");
			evt.printStackTrace();
		}

        
	}
	
	private void costCard(int cost, String cardid) {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT balance FROM ubike.cards where cardid='"+cardid+"';");
			
			int balance2 =0;
			
			if (rs.next()){
				balance2 = rs.getInt("balance") - cost;
			}	
			
			Statement stmt2 = con.createStatement();
			stmt2.executeUpdate("UPDATE cards SET balance = "+ balance2 +" WHERE cardid='"+cardid+"';");	
			
		} catch (SQLException evt) {
			evt.printStackTrace();
		}
		
	}
	
	

}