/**
 * 
 */
package sqlite;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import crypto.AESHelper;
import crypto.StreamHelper;
import logger.LoggerSetup;
import passguardui.PassGuardLoginController;

/**
 * @author cderuiter3
 *
 * This class contains the method to create, insert, and query 
 * the UserInfo table in the PassGuard SQLite Database.
 *
 */
public class SQLiteHelper {
	
	
	/**
     * Makes available a logger whose output is written to a file that is the
     * same name as this class.
     */
    private static final Logger LOGGER = LoggerSetup.initLogger(SQLitePassGuardLoginHelper.class.getName(),
    		SQLitePassGuardLoginHelper.class.getSimpleName() + ".log");
	


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
			LOGGER.log(Level.SEVERE, "Connection was not established");
			LOGGER.log(Level.SEVERE, e.getMessage());
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
				+ SQLiteContract.UserInfo.COLUMN_NOTES + " text\n," //can be null
				+ SQLiteContract.UserInfo.COLUMN_USERID + " text NOT NULL,\n" //this will match the primary id of the user in the login table
		        + "FOREIGN KEY (" + SQLiteContract.UserInfo.COLUMN_USERID 
		        + ") REFERENCES " + SQLiteContract.LoginInfo.TABLE_NAME +  "(" 
		        + SQLiteContract.LoginInfo._ID + ")"
				+ ");";

		try {
			Connection conn = DriverManager.getConnection(SQLiteContract.URL);
			Statement stmt = conn.createStatement();			
			stmt.execute(SQL_CREATE_TABLE);			
			} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Table was not created. Data will not be stored.");
			LOGGER.log(Level.SEVERE, e.getMessage());	
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
        		+ SQLiteContract.UserInfo.COLUMN_NOTES + ","
        		+ SQLiteContract.UserInfo.COLUMN_USERID + ") VALUES(?,?,?,?,?)";
        
		
		//String encryptedAccountName = StreamHelper.toByteString(AESHelper.encryptPBKDF2_AES(StreamHelper.writeToByteArray(AccountName), PassGuardLoginController.getCurrentUser()));;
		String encryptedUserName =   StreamHelper.toByteString(AESHelper.encryptPBKDF2_AES(StreamHelper.writeToByteArray(UserName), PassGuardLoginController.getCurrentUser()));
        String encryptedPassword = StreamHelper.toByteString(AESHelper.encryptPBKDF2_AES(StreamHelper.writeToByteArray(Password), PassGuardLoginController.getCurrentUser()));
        String encryptedNotes = StreamHelper.toByteString(AESHelper.encryptPBKDF2_AES(StreamHelper.writeToByteArray(Notes), PassGuardLoginController.getCurrentUser()));
 
        try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
        	PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_USERINFO)) {
            pstmt.setString(1, AccountName);
            pstmt.setString(2, encryptedUserName);
            pstmt.setString(3, encryptedPassword);
            pstmt.setString(4, encryptedNotes);
            pstmt.setString(5, PassGuardLoginController.getCurrentUser()); //this gets the primary key of the current user
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
				" FROM " + SQLiteContract.UserInfo.TABLE_NAME +
				" WHERE " + SQLiteContract.UserInfo.COLUMN_USERID + " = ?";
		

		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ALL_ACCOUNTS)) {
			pstmt.setString(1, PassGuardLoginController.getCurrentUser());//this restricts the current user to only accessing their data
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				AccountNames.add(rs.getString(SQLiteContract.UserInfo.COLUMN_ACCOUNT));
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "List of accounts for this user could not be retrieved");
			LOGGER.log(Level.WARNING, e.getMessage());
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
					username = 
							(String) StreamHelper.readFromByteArray(
							AESHelper.decryptPBKDF2_AES(
							StreamHelper.toByteArray( 
							rs.getString(SQLiteContract.UserInfo.COLUMN_USERNAME)), PassGuardLoginController.getCurrentUser()));
					
		}catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Requested Username could not be retrieved from the UserInfo Table");
			LOGGER.log(Level.SEVERE, e.getMessage());
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
			password = 
					(String) StreamHelper.readFromByteArray(
							AESHelper.decryptPBKDF2_AES(
							StreamHelper.toByteArray( 
							rs.getString(SQLiteContract.UserInfo.COLUMN_PASSWORD)), PassGuardLoginController.getCurrentUser()));
			
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Requested password could not be retrieved from the UserInfo Table");
			LOGGER.log(Level.SEVERE, e.getMessage());
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
			notes =
					(String) StreamHelper.readFromByteArray(
					AESHelper.decryptPBKDF2_AES(
					StreamHelper.toByteArray( 
					rs.getString(SQLiteContract.UserInfo.COLUMN_NOTES)), PassGuardLoginController.getCurrentUser()));

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Requested notes could not be retrieved from the UserInfo Table");
			LOGGER.log(Level.SEVERE, e.getMessage());
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
		
		
        String encryptedPassword = StreamHelper.toByteString(AESHelper.encryptPBKDF2_AES(StreamHelper.writeToByteArray(newPassword), PassGuardLoginController.getCurrentUser()));
 
        try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
                PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {
        	
            pstmt.setString(1, encryptedPassword);
            pstmt.setString(2, AccountName);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
        	LOGGER.log(Level.SEVERE, "Password was not updated in the UserInfo Table");
			LOGGER.log(Level.SEVERE, e.getMessage());
            System.out.println(e.getMessage());
        }
    }
	
	/**
     * Deletes a specific account's information from the SQLite Database
     *
     * @param AccountName - Account name that is being deleted
     *
     */
    public static void deleteAccount(String AccountName) {
        
    	String SQL_DELETE_ACCOUNT = 
    			"DELETE FROM " + SQLiteContract.UserInfo.TABLE_NAME + 
				" WHERE " + SQLiteContract.UserInfo.COLUMN_ACCOUNT + " = ?";
    	
    	
    	 try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
                 PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_ACCOUNT)) {
         	
             pstmt.setString(1, AccountName);
             pstmt.executeUpdate();
             
         } catch (SQLException e) {
         	LOGGER.log(Level.SEVERE, "Account not deleted");
 			LOGGER.log(Level.SEVERE, e.getMessage());
             System.out.println(e.getMessage());
         }
     }
        


    
    /**
     * Deletes all of the user's information from the SQLite Database
     *
     *
     */
    public static void deleteProfile() {
        
    	String SQL_DELETE_ACCOUNT = 
    			"DELETE FROM " + SQLiteContract.UserInfo.TABLE_NAME + 
				" WHERE " + SQLiteContract.UserInfo.COLUMN_USERID + " = ?";
    	
    	 try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
                 PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_ACCOUNT)) {
         	
             pstmt.setString(1, PassGuardLoginController.getCurrentUser());
             pstmt.executeUpdate();
             
         } catch (SQLException e) {
         	LOGGER.log(Level.SEVERE, "Account not deleted");
 			LOGGER.log(Level.SEVERE, e.getMessage());
             System.out.println(e.getMessage());
         }
        
}
	

	
}
