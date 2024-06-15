/**
 * User or Staff
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class UserOrStaff extends JFrame implements ActionListener{
	JButton user;
	JButton staff;
	
	UserOrStaff() {
		setLayout(new GridLayout(2,1,10,10));
		setVisible(true);
		setSize(400, 400);
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		user = new JButton("User Form");
		user.addActionListener(this);
		
		staff = new JButton("Staff Form");
		staff.addActionListener(this);
		
		this.add(user);
		this.add(staff);
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if(evt.getSource()==user) {
			//new MyFrame();
			new Login();
		}
		
		if(evt.getSource()==staff) {
			new Staff();
			dispose();
		}
		
		
		
	}
}