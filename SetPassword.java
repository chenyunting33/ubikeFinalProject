import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.*;

public class SetPassword extends JFrame implements ActionListener {
	JPasswordField newPassword;
	JLabel message, title;
	JLabel label_newPassword;
	JButton okbtn,cancel;
	JCheckBox showPassword;
	
	SetPassword(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 400);
		this.setTitle("set Password");
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		
		title = new JLabel("Password Setting");
		title.setBounds(300,80,200,40);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setForeground(Color.blue);
		
		label_newPassword = new JLabel("new password");
		label_newPassword.setBounds(200,150,100,40);
		//label_newPassword.setFont(new Font("Arial", Font.ITALIC, 14));
//		label_newPassword.setForeground(Color.blue);
		
		newPassword = new JPasswordField();
		newPassword.setBounds(300,150,300,40);
		
		message = new JLabel();
		message.setBounds(300,350,300,40);
		
		okbtn = new JButton("OK");
		okbtn.setBounds(420,200,100,40);
		okbtn.addActionListener(this);
		
		showPassword = new JCheckBox("Show Password");
		showPassword.setBounds(250,200,300,40);
		showPassword.addActionListener(this);
		
		this.add(title);
		this.add(message);
		this.add(label_newPassword);
		this.add(newPassword);
		this.add(okbtn);
		this.add(showPassword);
		this.setVisible(true);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(520,200,100,40);
		cancel.addActionListener(this);
		this.add(cancel);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource()==showPassword) {
			if(showPassword.isSelected()) {
				newPassword.setEchoChar((char)0);
			}
			else {
				newPassword.setEchoChar('*');
			}
		}	
		
		if(evt.getSource()==okbtn) {
			String newpw = new String(newPassword.getPassword());
			try {
				Connection con = DriverManager.getConnection
						("jdbc:mysql://localhost:3306/ubike","root","password");
				Statement stmt = con.createStatement();
				
				stmt.executeUpdate
				("UPDATE users SET `password` = '"+ newpw +"' WHERE `userid` = '"+ DatabaseHelper.signinData.userid +"';");
				
	            stmt.close();
				con.close();		
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			
			JOptionPane.showMessageDialog(this, "reset the password successfully");
			new Login();
			dispose();
		}
		if(evt.getSource()==cancel) {
			new Login();
			dispose();
		}
	}
}
