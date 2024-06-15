import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AddCard extends JFrame implements ActionListener {
	JTextField newcardid; 
	JLabel label_newcard;
	JButton okbtn,cancel;
	
	AddCard(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 400);
		this.setTitle("Add Card");
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		
		label_newcard = new JLabel("new card id");
		label_newcard.setBounds(70,120,150,40);
		this.add(label_newcard);
		
		newcardid = new JTextField();
		newcardid.setBounds(170,120,200,40);
		this.add(newcardid);
		
		okbtn = new JButton("Add");
		okbtn.setBounds(400,120,100,40);
		okbtn.addActionListener(this);
		this.add(okbtn);
		
		
		this.setVisible(true);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(400,170,100,40);
		cancel.addActionListener(this);
		this.add(cancel);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource()==okbtn) {
			String cardid = newcardid.getText();
			String id = DatabaseHelper.signinData.userid;

			try {
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
				Statement stmt = con.createStatement();
				stmt.executeUpdate
				("INSERT INTO cards (userid, cardid, balance) VALUES (" + id + ", '"+cardid+"', "+0+")");

				con.close();
			} catch (Exception exception) {
				exception.printStackTrace();				
			}
			
			JOptionPane.showMessageDialog(this, "Add card success, please charge.");
			new MyFrame();
			dispose();
		}
		if(evt.getSource()==cancel) {
			try {
				new Recharge();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dispose();
		}
	}
}
