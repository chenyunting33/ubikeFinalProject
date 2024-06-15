/**
 * User Top class
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;

public class MyFrame extends JFrame implements ActionListener{
	JButton login;
	JButton searchSpot;
	JButton searchRecord;
	JButton recharge;
	JButton rentBike;
	JButton returnBike;
	JButton repair;
	JLabel user;
	
	MyFrame() {
		setLayout(new GridLayout(4,2,10,10));
		setVisible(true);
		setSize(600, 400);
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		login = new JButton("Login Form");
		//login.setBounds(200,100,100,40);
		login.addActionListener(this);
		
		searchSpot = new JButton("Search spot");
		//searchSpot.setBounds(200,170,100,40);
		searchSpot.addActionListener(this);
		
		searchRecord = new JButton("Search record");
		//searchRecord.setBounds(200,240,100,40);
		searchRecord.addActionListener(this);
		
		recharge = new JButton("add card/recharge");
		//recharge.setBounds(200,310,100,40);
		recharge.addActionListener(this);
		
		rentBike = new JButton("rentBike");
		//rentBike.setBounds(600,100,100,40);
		rentBike.addActionListener(this);
		
		returnBike = new JButton("returnBike");
		returnBike.addActionListener(this);
		
		repair = new JButton("repair");
		repair.addActionListener(this);
		
		String phone = getphone(DatabaseHelper.signinData.userid);
		user = new JLabel("  currently login phone number: \n" + phone);
		
		this.add(login);
		this.add(searchSpot);
		this.add(searchRecord);
		this.add(recharge);
		this.add(rentBike);
		this.add(returnBike);
		this.add(repair);
		this.add(user);
		
	}
	
	private String getphone(String userid) {
		String ph = null;
		try {
			Connection con = DriverManager.getConnection
					("jdbc:mysql://localhost:3306/ubike","root","password");
			Statement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery
			("select phone from ubike.users WHERE `userid` = '"+ userid +"';");
			
			while(rs.next()) {
				ph = rs.getString("phone");
			}
			
            stmt.close();
			con.close();		
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return ph;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if(evt.getSource()==login) {
			new Login();
			dispose();
		}
		
		if(evt.getSource()==searchSpot) {
//			new StationQuery();
//			dispose();
			StationQuery frame = new StationQuery();
            frame.setVisible(true);
		}
		
		if(evt.getSource()==searchRecord) {
			new SearchRecord();
			dispose();
		}
		
		if(evt.getSource()==recharge) {
			try {
				new Recharge();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dispose();
		}
		
		if(evt.getSource()==repair) {
//			new MaintenanceReport();
//			dispose();
			MaintenanceReport frame = new MaintenanceReport();
			frame.setVisible(true);
		}
		
		if(evt.getSource()==rentBike) {
			if(DatabaseHelper.signinData.rentbike) {
				JOptionPane.showMessageDialog(this, "Return the bike first");
			}else {
				new RentBike();
				dispose();
			}
		}
		
		if(evt.getSource()==returnBike) {
			if(!DatabaseHelper.signinData.rentbike) {
				JOptionPane.showMessageDialog(this, "Rent a bike first");
			}else {
				new ReturnBike();
				dispose();
			}
		}
		
	}
}
