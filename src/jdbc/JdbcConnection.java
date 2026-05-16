package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcConnection {
	
	private static String host = "aws-0-eu-west-3.pooler.supabase.com";
	private static String base = "postgres";
	private static String user = "postgres.nivofwzpfeberatsxaxy";
	private static String password = "hugoettheoIED";
	private static String url = "jdbc:postgresql://" + host + ":6543/" + base;

	/**
	 * Singleton instance.
	 */
	private static Connection connection;

	public static Connection getConnection() {		
		if (connection == null) {
			try {
				connection = DriverManager.getConnection(url, user, password);
			} catch (Exception e) {
				System.err.println("Connection failed : " + e.getMessage());			
			}
		}
		return connection;
	}
}