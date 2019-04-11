/**
 * 
 */
package sqlite;

/**
 * @author cderuiter3
 * This class defines the names for SQLite database tables and columns
 *
 */
public class SQLiteContract {
	
	public static final String URL = "jdbc:sqlite:PassGuard.db";
	
	 private SQLiteContract() {
	    }

	    public static final class UserInfo{

	        public static final String TABLE_NAME = "UserInfo";
	        
	        public static final String _ID = "ID";
	        public static final String COLUMN_ACCOUNT = "AccountName";
	        public static final String COLUMN_USERNAME = "Username";
	        public static final String COLUMN_PASSWORD = "Password";
	        public static final String COLUMN_NOTES = "Notes";
	        public static final String COLUMN_USERID = "UserID"; //Foreign Key


	    }
	    
	    public static final class LoginInfo{

	        public static final String TABLE_NAME = "LoginInfo";
	        
	        public static final String _ID = "ID"; //Primary Key 
	        public static final String COLUMN_USERNAME = "Username";
	        public static final String COLUMN_PASSWORD = "Password";


	    }

}
