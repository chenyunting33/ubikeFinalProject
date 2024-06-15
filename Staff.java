// User Top class


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;

public class Staff extends JFrame implements ActionListener{
	JButton status;
	JButton cross;
	JButton dispatch,back;
	
	
	Staff() {
		setLayout(new GridLayout(2,2,10,10));
		setVisible(true);
		setSize(400, 400);
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		status = new JButton("Bike Status Form");
		//status.setBounds(200,100,100,40);
		status.addActionListener(this);
		
		cross = new JButton("Search Crossed");
		//cross.setBounds(200,170,100,40);
		cross.addActionListener(this);
		
		dispatch = new JButton("Bike Dispatching Form");
		//dispatch.setBounds(200,240,100,40);
		dispatch.addActionListener(this);
		
		back = new JButton("Back");
		//dispatch.setBounds(200,240,100,40);
		back.addActionListener(this);
		
		this.add(status);
		this.add(cross);
		this.add(dispatch);
		this.add(back);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if(evt.getSource()==status) {
			try {
				new BikeStatus();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dispose();
		}
		
		if(evt.getSource()==cross) {
			new BikeCross();
			dispose();
		}
		
		if(evt.getSource()==dispatch) {
			new BikeDispatch();
			dispose();
		}
		
		if(evt.getSource()==back) {
			new UserOrStaff();
			dispose();
		}
		
		
	}
}
