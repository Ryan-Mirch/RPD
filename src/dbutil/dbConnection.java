package dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {
    private static final String USERNAME = "sql9239084";
    private static final String PASSWORD = "RE17QelMTL";
    private static final String CONN = "jdbc:mysql://sql9.freemysqlhosting.net:3306/sql9239084";
    private static final String SQCONN = "jdbc:sqlite:RPMDB.sqlite";


    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(CONN,USERNAME,PASSWORD);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		/*try {
			//Class.forName("org.sqlite.JDBC");
			//return DriverManager.getConnection(SQCONN);

			//Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(CONN,USERNAME,PASSWORD);
		}

		catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}*/
        return null;
    }
}
