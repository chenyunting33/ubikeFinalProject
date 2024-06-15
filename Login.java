import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.*;

public class Login extends JFrame implements ActionListener{

	JPasswordField password;
	JTextField username;
	JLabel label_password, label_username, message, title;
	JButton login;
	JButton signup;
	JButton setPassword,back;
	JCheckBox showPassword;
	
	Login(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setTitle("Login form");
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		
		title = new JLabel("Login Form");
		title.setBounds(370,80,280,40);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		title.setForeground(Color.blue);
		
		label_username = new JLabel("Phone Number");
		label_username.setBounds(200,150,100,40);
		//label_username.setFont(new Font("Arial", Font.ITALIC, 14));
//		label_username.setForeground(Color.blue);
		
		label_password = new JLabel("Password");
		label_password.setBounds(200,200,100,40);
		
		username = new JTextField();
		username.setBounds(300,150,300,40);
		
		password = new JPasswordField();
		password.setBounds(300,200,300,40);
		
		message = new JLabel();
		message.setBounds(300,350,300,40);
		
		login = new JButton("Sign in");
		login.setBounds(320,300,100,40);
		login.addActionListener(this);
		
		signup = new JButton("Sign up");
		signup.setBounds(460,290,130,20);
		signup.addActionListener(this);
		
		setPassword = new JButton("Password Setting");
		setPassword.setBounds(460,260,130,20);
		setPassword.addActionListener(this);
		
		showPassword = new JCheckBox("Show Password");
		showPassword.setBounds(300,250,300,40);
		showPassword.addActionListener(this);
		
		this.add(title);
		this.add(message);
		this.add(label_username);
		this.add(username);
		this.add(label_password);
		this.add(password);
		this.add(login);
		this.add(signup);
		this.add(setPassword);
		this.add(showPassword);
		this.setVisible(true);
		
		back = new JButton("Back");
		back.setBounds(200,300,100,40);
		back.addActionListener(this);
		this.add(back);
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		//press Sign in
		if(evt.getSource()==login){
			String userText = username.getText();
			String pwdText = new String(password.getPassword());
			
			boolean exists = DatabaseHelper.isDataExist(userText);
			boolean correctpw = DatabaseHelper.isCorrectpw(userText,pwdText);
	        
	        if (!exists) {
	        	JOptionPane.showMessageDialog(this, "We cannot find you");
	        } else if(!correctpw) {
	        	JOptionPane.showMessageDialog(this, "Invalid passsword");
	        }else {
	            DatabaseHelper.signinData.setNewID(userText);
	            JOptionPane.showMessageDialog(this, "Sign in successfully!");
	        }
		}
		
		if(evt.getSource()==showPassword) {
			if(showPassword.isSelected()) {
				password.setEchoChar((char)0);
			}
			else {
				password.setEchoChar('*');
			}
		}
		
		if(evt.getSource()==signup) {
			new Signup();
			dispose();
			//message.setText("sign up function haven't done");
		}
		
		if(evt.getSource()==setPassword) {
			if(DatabaseHelper.signinData.userstatus) {
				new SetPassword();
				dispose();
			}else {
				JOptionPane.showMessageDialog(this, "Please sign in!");
			}
			
		}
		
		if(evt.getSource()==back) {
			if(DatabaseHelper.signinData.userstatus) {
				new MyFrame();
				dispose();
			}else {
				JOptionPane.showMessageDialog(this, "Please sign in!");
			}
		}
	
		
//		String msg = "Your username is : " + username.getText();
//		msg += " and your password is : " + new String(password.getPassword());
		
		//message.setText(msg);
		
		
	}
	
}