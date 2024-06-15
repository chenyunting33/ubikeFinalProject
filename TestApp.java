import java.sql.*;

public class TestApp {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		Class.forName("com.mysql.cj.jdbc.Driver");
//		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ubike","root","password");
//		//.out.println("success");
//		
//		Statement stmt = con.createStatement();
//		ResultSet rs = stmt.executeQuery("select * from ubike.bikes where BikeUID='A02052'");
//		
//		while (rs.next()){  
//			String uid = rs.getString("Type");
//			//System.out.println(uid);
//        }
//		
//		Statement stmt2 = con.createStatement();
//		//String str = getString();
////		stmt2.executeUpdate("UPDATE bikes SET `type` = 'Broken' WHERE `BikeUID` = '"+str+"';");
//				
//		rs.close(); 
//        con.close();
//		
		UserOrStaff obj = new UserOrStaff();
		//SetPassword obj2 = new SetPassword();
//		Signup obj3 = new Signup();
//		MyFrame obj4 = new MyFrame();
//        BikeStatus obj5 = new BikeStatus();
//       BikeDispatch obj6 = new BikeDispatch();
//        Recharge obj7 = new Recharge();
//        BikeCross obj8 = new BikeCross();
//        AddCard obj9 = new AddCard();
//		RentBike obj10 = new RentBike();
//		ReturnBike obj11 = new ReturnBike();
		
		
	}

}

/*
 * UserOrStaff 	- MyFrame(User) - Login - SetPassword
 * 										- Signup
 * 								- StationQuery(SearchSpot)
 * 								- SearchRecord
 * 								- Recharge - AddCard
 * 								- RentBike
 * 								- ReturnBike
 * 								- MaintenanceReport
 * 				- Staff -車輛狀態 BikeStatus
 * 						-跨區車輛 BikeCross
 * 						-車輛調度 BikeDispatch
 * 	
 */
