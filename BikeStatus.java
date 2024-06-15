import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class BikeStatus extends JFrame implements ActionListener {
	JLabel uidPlease, staPlease;
	JTextField bikeuidEnter, statusEnter;
	JButton search, change, cancel;
	JLabel mes;
	
	BikeStatus() throws SQLException {
		setLayout(null);
		setVisible(true);
		setSize(600, 400);
		this.setLocationRelativeTo(null);
		this.setTitle("Bike Status");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		uidPlease = new JLabel("Enter Bike UID ");
		uidPlease.setBounds(70,100,200,40);
		
		bikeuidEnter = new JTextField();
		bikeuidEnter.setBounds(170,100,200,40);
		
		mes = new JLabel();
		mes.setBounds(70,200,300,40);
		
		search = new JButton("search");
		search.setBounds(380,100,100,40);
		search.addActionListener(this);
		
		this.add(uidPlease);
		this.add(bikeuidEnter);
		this.add(search);
		this.add(mes);
		
		staPlease = new JLabel("Status change to ");
		staPlease.setBounds(60,150,200,40);
		this.add(staPlease);
		
		statusEnter = new JTextField();
		statusEnter.setBounds(170,150,200,40);
		this.add(statusEnter);
		
		change = new JButton("change");
		change.setBounds(380,150,100,40);
		change.addActionListener(this);
		this.add(change);
		
		cancel = new JButton("Cancel/Back");
		cancel.setBounds(380,250,100,40);
		cancel.addActionListener(this); 
		this.add(cancel);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String bikeuid = bikeuidEnter.getText();
		if(e.getSource()==search){
			try {
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("select * from ubike.bikes where BikeUID='" + bikeuid + "'");
				if (rs.next()){  
					String status = rs.getString("Type");
					mes.setText("The status is " + status );
		        }
				else {
					mes.setText("Wrong bikeUID" );
				}
				rs.close();
				con.close();
			} catch (Exception exception) {
				exception.printStackTrace();				
			}			
		}
		String newstatus = statusEnter.getText();
		if(e.getSource()==change) {
			try {
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
				Statement stmt = con.createStatement();
				stmt.executeUpdate("UPDATE bikes SET `type` = '" + newstatus + "' WHERE `BikeUID` = '" + bikeuid +"';");
				
				ResultSet rs = stmt.executeQuery("select * from ubike.bikes where BikeUID='" + bikeuid + "'");
				while (rs.next()){  
					String status = rs.getString("Type");
					mes.setText("The status has changed to " + status );
		        }
				rs.close();
				con.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		
		if(e.getSource()==cancel) {
			dispose();
			new Staff();

		}
	}	
}
