/**
 * 
 */
package sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import crypto.AESHelper;
import crypto.StreamHelper;
import logger.LoggerSetup;
import passguardui.PassGuardLoginController;

/**
 * This class contains the method to create and interact with the LoginInfo table
 * in the SQLite DB
 * The LoginInfo contains the usernames and passwords for PassGuard Accounts
 * 
 * @author cderuiter3
 *
 */
public class SQLitePassGuardLoginHelper {
	
	
	/**
     * Makes available a logger whose output is written to a file that is the
     * same name as this class.
     */
    private static final Logger LOGGER = LoggerSetup.initLogger(SQLitePassGuardLoginHelper.class.getName(),
    		SQLitePassGuardLoginHelper.class.getSimpleName() + ".log");
	

	
	/**
	 * Create a table in the SQLite Database called LoginInfo 
	 * 
	 * This will likely only be used when 
	 * when users are creating a PassGuard Account
	 * and it will store their selected username and
	 * password as well as their primary key which acts as 
	 * each user's User ID
	 *
	 */
	
	public static void createNewAccountTable() {

		
		String SQL_CREATE_TABLE = 
		"CREATE TABLE IF NOT EXISTS " + SQLiteContract.LoginInfo.TABLE_NAME + "(\n" 
		+ SQLiteContract.LoginInfo._ID + " integer PRIMARY KEY,\n"
		+ SQLiteContract.LoginInfo.COLUMN_USERNAME + " text NOT NULL,\n"
		+ SQLiteContract.LoginInfo.COLUMN_PASSWORD + " text NOT NULL\n"
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
	 * Inserts login info into SQLite Database LoginInfo table
	 * The primary key for every username and password pair
	 * is auto-populated and will act as the Users ID
	 * 
	 * @param UserName
	 * @param Password
	 *
	 */
	
	public static void insertLoginInfo(String UserName, String Password) {
        String SQL_INSERT_LOGININFO = "INSERT INTO LoginInfo("
        		+ SQLiteContract.LoginInfo.COLUMN_USERNAME + ","
        		+ SQLiteContract.LoginInfo.COLUMN_PASSWORD
        		+ ") VALUES(?,?)";
        
        String encryptedPassword = StreamHelper.toByteString(AESHelper.encryptPBKDF2_AES(StreamHelper.writeToByteArray(Password), UserName));
 
        try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
        	PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_LOGININFO)) {
            pstmt.setString(1, UserName);
            pstmt.setString(2, encryptedPassword );
            pstmt.executeUpdate();
        } catch (SQLException e) {
        	LOGGER.log(Level.SEVERE, "Entry was not inserted into the table");
			LOGGER.log(Level.SEVERE, e.getMessage());
            System.out.println(e.getMessage());
        }
    }
	
	/**
	 * Retrieves the username for a specified account in the SQLite Database LoginInfo table 
	 * 
	 * @param Username - name of the account that corresponds to the desired username 
	 * 
	 * @return username - username for a specific account
	 * 
	 *
	 */

	public static String getUsername(String Username) {
		String username = null;
		
		String SQL_GET_USERNAME = 
				"SELECT " + SQLiteContract.LoginInfo.COLUMN_USERNAME + 
				" FROM " + SQLiteContract.LoginInfo.TABLE_NAME + 
				" WHERE " + SQLiteContract.LoginInfo.COLUMN_USERNAME + " = ?";
		
		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				PreparedStatement pstmt = conn.prepareStatement(SQL_GET_USERNAME)){
					
					pstmt.setString(1, Username);					
					ResultSet rs = pstmt.executeQuery();
					username = rs.getString(SQLiteContract.LoginInfo.COLUMN_USERNAME);
					
		}catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Requested Username could not be retreived from the LoginInfo Table");
			LOGGER.log(Level.WARNING, e.getMessage());
			System.out.println(e.getMessage());
		}
		return username;
	}
	
	/**
	 * Retrieves the primary key from the Login table 
	 * for a specified username in the SQLite Database 
	 * 
	 * @param username - username that corresponds to the desired primary key
	 *  
	 * @return UserId - primary key integer
	 * 
	 * This number is used as the foreign key in the UserInfo 
	 * table and is the link between each PassGuard account and 
	 * their corresponding data
	 * 
	 *
	 */

	public static int getUserID(String username) {
		int id = -1;
		
		String SQL_GET_ID = 
				"SELECT " + SQLiteContract.LoginInfo._ID + 
				" FROM " + SQLiteContract.LoginInfo.TABLE_NAME + 
				" WHERE " + SQLiteContract.LoginInfo.COLUMN_USERNAME + " = ?";
		
		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				PreparedStatement pstmt = conn.prepareStatement(SQL_GET_ID)){
					
					pstmt.setString(1, username);					
					ResultSet rs = pstmt.executeQuery();
					id = rs.getInt(SQLiteContract.LoginInfo._ID);
					
		}catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Requested User ID could not be retrieved from the LoginInfo Table");
			LOGGER.log(Level.WARNING, e.getMessage());
		}
		return id;
	}
	
	
	
	
	/**
	 * Retrieves the password for a specified username in the SQLite Database
	 * This method can be used to check if a username and password pair are correct 
	 * 
	 * @param UserName - the username that corresponds to the desired password 
	 * 
	 * @return password - password for a specific account
	 * 
	 *
	 */

	public static String getPassword(String UserName) {
		String password = null;
		
		String SQL_GET_PASSWORD = 
				"SELECT " + SQLiteContract.LoginInfo.COLUMN_PASSWORD + 
				" FROM " + SQLiteContract.LoginInfo.TABLE_NAME + 
				" WHERE " + SQLiteContract.LoginInfo.COLUMN_USERNAME + " = ?";
		
		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				PreparedStatement pstmt = conn.prepareStatement(SQL_GET_PASSWORD)) {

			pstmt.setString(1, UserName);
			ResultSet rs = pstmt.executeQuery();
			password = 
					(String) StreamHelper.readFromByteArray(
							AESHelper.decryptPBKDF2_AES(
							StreamHelper.toByteArray( 
							rs.getString(SQLiteContract.LoginInfo.COLUMN_PASSWORD)), SQLitePassGuardLoginHelper.getUsername(UserName)));
					
			
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Requested password could not be retrieved from the LoginInfo Table");
			LOGGER.log(Level.WARNING, e.getMessage());
			System.out.println(e.getMessage());
		}
		return password;
	}
	
	public static String getEncryptedPassword(String UserName) {
		String password = null;
		
		String SQL_GET_PASSWORD = 
				"SELECT " + SQLiteContract.LoginInfo.COLUMN_PASSWORD + 
				" FROM " + SQLiteContract.LoginInfo.TABLE_NAME + 
				" WHERE " + SQLiteContract.LoginInfo.COLUMN_USERNAME + " = ?";
		
		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				PreparedStatement pstmt = conn.prepareStatement(SQL_GET_PASSWORD)) {

			pstmt.setString(1, UserName);
			ResultSet rs = pstmt.executeQuery();
			password = 	rs.getString(SQLiteContract.LoginInfo.COLUMN_PASSWORD);
					
			
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Requested password could not be retrieved from the LoginInfo Table");
			LOGGER.log(Level.WARNING, e.getMessage());
			System.out.println(e.getMessage());
		}
		return password;
	}
	
	/**
	 * Retrieves all usernames stored in the SQLite Database
	 * LoginInfo Table	  
	 * 
	 * @return UserNames - ArrayList of Strings of all usernames
	 * 
	 *
	 */
	public static ArrayList<String> getAllLoginUserNames() {
		ArrayList<String> UserNames = new ArrayList<String>();

		String SQL_GET_ALL_USERNAMES = 
				"SELECT " + SQLiteContract.LoginInfo.COLUMN_USERNAME + 
				" FROM " + SQLiteContract.LoginInfo.TABLE_NAME;

		try (Connection conn = DriverManager.getConnection(SQLiteContract.URL);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(SQL_GET_ALL_USERNAMES)) {
			while (rs.next()) {
				UserNames.add(rs.getString(SQLiteContract.LoginInfo.COLUMN_USERNAME));
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "List of usernames could not be retreived from the LoginInfo Table");
			LOGGER.log(Level.WARNING, e.getMessage());
			System.out.println(e.getMessage());
		}
		return UserNames;
	}
	

}
