/**
 * 
 */
package sqlite;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author cderuiter3
 *
 * This class contains the method to create, insert, and query 
 * the UserInfo table in the PassGuard SQLite Database.
 *
 */
public class SQLiteHelper {

	/**
	 * Get connection to the SQLite Database 
	 * 
	 * If the SQLite Database has not yet been created, 
	 * it will be created and should be located in same directory
	 * as the application. This will likely only occur 
	 * when users are creating a PassGuard Account
	 *
	 *@return conn - connection to the SQLite Database (Could be Null)
	 */
	public static Connection SQLLiteDatabaseConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(SQLiteContract.URL);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}
	
	/**
	 * Create UserInfo Table in SQLite Database 
	 * 
	 * This will likely only be used when 
	 * when users are creating a PassGuard Account
	 *
	 */
	
	public static void createNewTable() {

		
				String SQL_CREATE_TABLE = 
				"CREATE TABLE IF NOT EXISTS " + SQLiteContract.UserInfo.TABLE_NAME + "(\n" 
				+ SQLiteContract.UserInfo._ID + " integer PRIMARY KEY,\n"
				+ SQLiteContract.UserInfo.COLUMN_ACCOUNT + " text NOT NULL,\n" 
				+ SQLiteContract.UserInfo.COLUMN_USERNAME + " text NOT NULL,\n"
				+ SQLiteContract.UserInfo.COLUMN_PASSWORD + " text NOT NULL,\n"
				+ SQLiteContract.UserInfo.COLUMN_NOTES + " text NOT NULL\n"
				+ ");";

		try {
			Connection conn = DriverManager.getConnection(SQLiteContract.URL);
			Statement stmt = conn.createStatement();			
			stmt.execute(SQL_CREATE_TABLE);			
			} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	/**
	 * Inserts users info into SQLite Database 
	 * 
	 * @param AccountName
	 * @param UserName
	 * @param Password
	 * @param Notes
	 * 
	 * Note: Notes cannot be null as of right now
	 *
	 */
	public static void insertInfo(String AccountName, String UserName, String Password, String Notes) {
        String SQL_INSERT_USERINFO = "INSERT INTO UserInfo("
        		+ SQLiteContract.UserInfo.COLUMN_ACCOUNT + ","
        		+ SQLiteContract.UserInfo.COLUMN_USERNAME + ","
        		+ SQLiteContract.UserInfo.COLUMN_PASSWORD + ","
        		+ SQLiteContract.UserInfo.COLUMN_NOTES + ") VALUES(?,?,?,?)";
 
        try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
        	PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_USERINFO)) {
            pstmt.setString(1, AccountName);
            pstmt.setString(2, UserName);
            pstmt.setString(3, Password);
            pstmt.setString(4, Notes);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	/**
	 * Retrieves all account names stored in the SQLite Database 
	 * 
	 * @return AccountNames - ArrayList of Strings of all accounts
	 * 
	 *
	 */
	public static ArrayList<String> getAllAccounts() {
		ArrayList<String> AccountNames = new ArrayList<String>();

		String SQL_GET_ALL_ACCOUNTS = 
				"SELECT " + SQLiteContract.UserInfo.COLUMN_ACCOUNT + 
				" FROM " + SQLiteContract.UserInfo.TABLE_NAME;

		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(SQL_GET_ALL_ACCOUNTS)) {
			while (rs.next()) {
				AccountNames.add(rs.getString(SQLiteContract.UserInfo.COLUMN_ACCOUNT));
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return AccountNames;
	}
	
	/**
	 * Retrieves the username for a specified account in the SQLite Database 
	 * 
	 * @param AccountName - name of the account that corresponds to the desired username 
	 * 
	 * @return username - username for a specific account
	 * 
	 *
	 */

	public static String getUsername(String AccountName) {
		String username = null;
		
		String SQL_GET_USERNAME = 
				"SELECT " + SQLiteContract.UserInfo.COLUMN_USERNAME + 
				" FROM " + SQLiteContract.UserInfo.TABLE_NAME + 
				" WHERE " + SQLiteContract.UserInfo.COLUMN_ACCOUNT + " = ?";
		
		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				PreparedStatement pstmt = conn.prepareStatement(SQL_GET_USERNAME)){
					
					pstmt.setString(1, AccountName);					
					ResultSet rs = pstmt.executeQuery();
					username = rs.getString(SQLiteContract.UserInfo.COLUMN_USERNAME);
					
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return username;
	}
	
	/**
	 * Retrieves the password for a specified account in the SQLite Database 
	 * 
	 * @param AccountName - name of the account that corresponds to the desired password 
	 * 
	 * @return password - password for a specific account
	 * 
	 *
	 */

	public static String getPassword(String AccountName) {
		String password = null;
		
		String SQL_GET_PASSWORD = 
				"SELECT " + SQLiteContract.UserInfo.COLUMN_PASSWORD + 
				" FROM " + SQLiteContract.UserInfo.TABLE_NAME + 
				" WHERE " + SQLiteContract.UserInfo.COLUMN_ACCOUNT + " = ?";
		
		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				PreparedStatement pstmt = conn.prepareStatement(SQL_GET_PASSWORD)) {

			pstmt.setString(1, AccountName);
			ResultSet rs = pstmt.executeQuery();
			password = rs.getString(SQLiteContract.UserInfo.COLUMN_PASSWORD);
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return password;
	}
	
	/**
	 * Retrieves the notes for a specified account in the SQLite Database 
	 * 
	 * @param AccountName - name of the account that corresponds to the desired notes 
	 * 
	 * @return notes - notes for a specific account
	 * 
	 *
	 */

	public static String getNotes(String AccountName) {
		String notes = null;
		
		String SQL_GET_NOTES = 
				"SELECT " + SQLiteContract.UserInfo.COLUMN_NOTES + 
				" FROM " + SQLiteContract.UserInfo.TABLE_NAME + 
				" WHERE " + SQLiteContract.UserInfo.COLUMN_ACCOUNT + " = ?";
		
		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				PreparedStatement pstmt = conn.prepareStatement(SQL_GET_NOTES)) {

			pstmt.setString(1, AccountName);
			ResultSet rs = pstmt.executeQuery();
			notes = rs.getString(SQLiteContract.UserInfo.COLUMN_NOTES);
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return notes;
	}
	
	/**
	 * Changes the stored password for a specified account in the SQLite Database 
	 * 
	 * @param AccountName - name of the account that corresponds to the password being changed 
	 * @param newPassword - new password for the specified account
	 * 
	 */
	public static void updatePassword(String AccountName, String newPassword) {
        
		String SQL_UPDATE_PASSWORD = 
				"UPDATE " + SQLiteContract.UserInfo.TABLE_NAME + 
				" SET " + SQLiteContract.UserInfo.COLUMN_PASSWORD +" = ? WHERE " 
						+ SQLiteContract.UserInfo.COLUMN_ACCOUNT + " = ?";
 
        try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
                PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {
        	
            pstmt.setString(1, newPassword);
            pstmt.setString(2, AccountName);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	
}
