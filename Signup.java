import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.*;

public class Signup extends JFrame implements ActionListener {
	JTextField phone; 
	JPasswordField password;
	JLabel message, title;
	JLabel label_phone, label_password;
	JButton okbtn,cancel;
	JCheckBox showPassword;
	
	Signup(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setTitle("sign up");
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		
		title = new JLabel("Sign Up Form");
		title.setBounds(300,80,280,40);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setForeground(Color.blue);
		
		label_phone = new JLabel("your phone number");
		label_phone.setBounds(150,150,150,40);
		label_phone.setFont(new Font("Arial", Font.ITALIC, 14));
		label_phone.setForeground(Color.blue);
		
		label_password = new JLabel("your password");
		label_password.setBounds(150,200,150,40);
		
		phone = new JTextField();
		phone.setBounds(300,150,300,40);
		
		password = new JPasswordField();
		password.setBounds(300,200,300,40);
		
		message = new JLabel();
		message.setBounds(300,350,300,40);
		
		okbtn = new JButton("Sign Up");
		okbtn.setBounds(500,260,100,40);
		okbtn.addActionListener(this);
		
		showPassword = new JCheckBox("Show Password");
		showPassword.setBounds(300,250,300,40);
		showPassword.addActionListener(this);
		
		this.add(title);
		this.add(message);
		this.add(label_password);
		this.add(password);
		this.add(label_phone);
		this.add(phone);
		this.add(okbtn);
		this.add(showPassword);
		this.setVisible(true);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(500,310,100,40);
		cancel.addActionListener(this);
		this.add(cancel);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource()==showPassword) {
			if(showPassword.isSelected()) {
				password.setEchoChar((char)0);
			}
			else {
				password.setEchoChar('*');
			}
		}	
		
		if(evt.getSource()==okbtn) {
			String newphone = phone.getText();
			String newpw = new String(password.getPassword());
			
			try {
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
				Statement stmt = con.createStatement();
				stmt.executeUpdate
				("INSERT INTO users (phone, password) VALUES ('"+newphone+"', '"+newpw+"')");
				
				
				con.close();
			} catch (Exception exception) {
				exception.printStackTrace();				
			}
			
			JOptionPane.showMessageDialog(this, "Sign up success, already login.");
			new MyFrame();
			dispose();
		}
		if(evt.getSource()==cancel) {
			new Login();
			dispose();
		}
	}
}