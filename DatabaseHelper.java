import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://localhost:3306/ubike";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public class signinData{
    	public static String userid = "1";  
    	public static boolean userstatus = true;//login status
    	public static boolean rentbike = false;
    	public static String rentTime = "2024-06-15 05:00:00";
    	
//    	public static String userid ;  
//    	public static boolean userstatus = false;

	    // 修改靜態變量的方法
	    public static void setNewID(String signinPhone) {
	        userstatus = true;
//	        try {
//				Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
//				Statement stmt = con.createStatement();
//				ResultSet rs = stmt.executeQuery
//						("select userid from ubike.users where phone ='"+signinPhone+"'");
//				
//				while (rs.next()){  
//					userid = rs.getString("userid");
//				}		
//				rs.close();
//				con.close();		
//			} catch (Exception exception) {
//				exception.printStackTrace();
//			}
	    }
	    
	    public static void setRent() {
	    	rentbike = true;
	    }
	    
	    public static void setReturn() {
	    	rentbike = false;
	    }
	    
	    public static void setRentTime(String dateTimeStr) {
	    	rentTime = dateTimeStr;
	    }
    }
    
    
    // 檢查是否存在特定 phone 值的數據
    public static boolean isDataExist(String phone) {
        String query = "SELECT 1 FROM users WHERE phone = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();  // 如果存在記錄，返回 true
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean isCorrectpw(String phone, String pw) {
    	String query = "SELECT password FROM users WHERE phone = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
            	if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    return pw.equals(dbPassword); // 比較密碼是否匹配
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
}
