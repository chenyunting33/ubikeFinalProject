import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

public class Recharge extends JFrame implements ActionListener {
	JLabel chooseText, chargeText;
	JTextField money;
	JLabel balance;
	JButton addcard, change, cancel;
	JLabel mes;
	private JComboBox<String> cardsid;
	private JComboBox<Integer> moneybox;
	JFormattedTextField numberField;
	
	Recharge() throws SQLException {
		setLayout(null);
		setVisible(true);
		setSize(600, 400);
		this.setLocationRelativeTo(null);
		this.setTitle("Recharge/ Add Card");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		chooseText = new JLabel("Choose Card");
		chooseText.setBounds(70,100,200,40);
		this.add(chooseText);
		
		cardsid = new JComboBox();
		cardsid.setBounds(170,100,200,40);
		loadData();
		this.add(cardsid);
		
//		balance = new JLabel("balance");
//		balance.setBounds(380,100,200,40);
//		this.add(balance);
		
//		showSelectedItem();
		
		//add card
		addcard = new JButton("add card");
		addcard.setBounds(380,250,100,40);
		addcard.addActionListener(this);
		this.add(addcard);
		
		
		chargeText = new JLabel("charge amount");
		chargeText.setBounds(60,150,200,40);
		this.add(chargeText);
		
		moneybox = new JComboBox();
		moneybox.setBounds(170,150,200,40);
		loadmoney();
		this.add(moneybox);
		  
		change = new JButton("charge");
		change.setBounds(380,150,100,40);
		change.addActionListener(this);
		this.add(change);
		
		cancel = new JButton("Cancel/Back");
		cancel.setBounds(120,250,100,40);
		cancel.addActionListener(this); 
		this.add(cancel);
		
	}
	
	private void loadData() {
		try {
			Connection con = DriverManager.getConnection
					("jdbc:mysql://localhost:3306/ubike","root","password");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from ubike.cards");
			
			while (rs.next()){  
				String userid = rs.getString("userid");
				if(userid.equals(DatabaseHelper.signinData.userid)) {
					String cardid = rs.getString("cardid");
					cardsid.addItem(cardid);
				}
			}		
			rs.close();
			con.close();		
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private void loadmoney() {
		moneybox.addItem(100);
		moneybox.addItem(200);
		moneybox.addItem(500);
		moneybox.addItem(1000);
	}

	//顯示餘額
//	private void showSelectedItem() {
//		 try {
//			String selectedItem = (String) cardsid.getSelectedItem();
//			System.out.println(selectedItem);
//		    String query = "SELECT balance FROM ubike.cards WHERE cardid = ?";
//		    try (Connection con = DriverManager.getConnection
//		    		("jdbc:mysql://localhost:3306/ubike", "root", "password");
//		         PreparedStatement stmt = con.prepareStatement(query)) {
//		        stmt.setString(1, selectedItem);
//		        try (ResultSet rs = stmt.executeQuery()) {
//		            if (rs.next()) {
//		                String ss = rs.getString("balance");
//		                balance.setText("Remains "+ss+"NTD.");
//		            } else {
//		            	balance.setText("No card found");
//		            }
//			        }
//			    }
//			} catch (SQLException e) {
//			    e.printStackTrace();
//			}
//		}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		   
		int amount = (int) moneybox.getSelectedItem();
		
		String cardid = (String) cardsid.getSelectedItem();
		int oldbalance = 0;
		int newbalance = 0;
		
		if(e.getSource()==change){
			try {
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
				Statement stmt = con.createStatement();
				
				ResultSet rs = stmt.executeQuery("select balance from ubike.cards where cardid='" + cardid + "'");
				if (rs.next()){   
					oldbalance = rs.getInt("balance");
		        }
				
				newbalance = oldbalance + amount;
				
				stmt.executeUpdate
				("UPDATE cards SET `balance` = '"+ newbalance +"' WHERE `cardid` = '"+ cardid +"';");
			
				rs.close();
				con.close();
			} catch (Exception exception) {
				exception.printStackTrace();				
			}			
		}
		
		
		if(e.getSource()==addcard) {
			dispose();
			new AddCard();
		}
		
		if(e.getSource()==cancel) {
			dispose();
			new MyFrame();
		}
	}	
}