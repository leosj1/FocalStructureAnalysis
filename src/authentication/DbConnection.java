package authentication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnection {
	public static HashMap<String, String> loadConstant() {
		HashMap<String, String> hm = new HashMap<String, String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("C:/fsa.config")); // Read the config file
			String temp = ""; // Temporary variable to loop through the content of the file
			String[] arr;
			while ((temp = br.readLine()) != null) {
				temp = temp.trim(); // Strip the whitespaces
				if (temp.isEmpty() || temp.startsWith("//")) {
					continue; // Skip the comments, for example the author, created on and document type
				} else {
					arr = temp.split("##"); // Split it by ##, for example, if you have name##wale, then arr[0] = name
											// and arr[1] = wale and arr.length = 2 since it contains 2 elements
					if (arr.length == 2) {
						hm.put(arr[0].trim(), arr[1].trim()); // Save the element as a key value pair. Using example
																// above, the Hashmap will be [user, wale], where user
																// is the key
					}
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE,
					"Encounter error while loading config file", ex); // To log the error for this specific class
		}
		return hm;
	}

	/**
	 * For getting the connection parameter and connecting to the database driver
	 * 
	 * @return connection
	 */
	public Connection getConnection() {
		try {
			HashMap<String, String> hm = new HashMap<String, String>();

			hm = DbConnection.loadConstant(); // load the connection parameter so we can fetch appropriate parameters
												// like username, password, etc
			String connectionURL = hm.get("dbConnection"); // "jdbc:mysql://localhost:3306/blogtrackers";
			String driver = hm.get("driver");
			String username = hm.get("dbUserName");// "root";//
			String password = hm.get("dbPassword");

			if (connectionURL != null && username != null && password != null) { // check to see if the connection
																					// parameter was successfully loaded
				try {
					Class.forName(driver); // com.mysql.jdbc.Driver
					// load the connection driver
				} catch (ClassNotFoundException ex) { // since this class can throw ClassNotFoundException so we are
														// catching it
					ex.printStackTrace();
					System.out.println("Encounter error while connecting to the database");// if there is an exception,
																							// give us a stacktrace of
																							// it
				}
			}
			Connection conn = DriverManager.getConnection(connectionURL, username, password); // create an instance of
																								// the connection using
																								// the JDBC driver
			return conn;
		} catch (SQLException ex) {
			Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE,
					"Encounter error while connecting to the database", ex); // Log the error for this specific class
		}
		return null; // Returns nothing if the connection is not successful
	}
	
	/**
	 * Custom query for getting ResultSet
	 * 
	 * @param query query string for Keyword Trend
	 * @return ResultSet result
	 */
	public ResultSet queryResultSet(String query) {
		ResultSet rs = null;
		try {
			Connection conn = getConnection();
			Statement stmt = null;

			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery(query);

		} catch (Exception e) {
			System.out.println("test");
		}
		return rs;
	}
	
	/**
	 * Query Database
	 * 
	 * @param query query string
	 * @return arraylist result
	 */
	public List<ArrayList<String>> query(String query) {
		List<ArrayList<String>> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			ResultSet rs = null;
			Statement stmt = null;
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int column_size = rsmd.getColumnCount();
			int i = 0;
			while (rs.next()) {
				ArrayList<String> output = new ArrayList<>();
				int total = column_size;
				for (int j = 1; j <= (total); j++) {
					output.add((j - 1), rs.getString(j));
				}
				result.add(i, output);
				i++;
			}

			rs.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Update table query
	 * 
	 * @param query MySQL query string
	 * @return boolean; True if table updated and False if otherwise
	 */
	public boolean updateTable(String query) {

		boolean donee = false;
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(query);

			pstmt.executeUpdate(query);
			donee = true;
			pstmt.close();
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		return donee;
	}
	
	/**
	 * calculates the MD5 hash of a string.
	 * 
	 * @param userNamePass user password
	 * @return hashed string
	 */
	public String md5Funct(String userNamePass) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(userNamePass.getBytes());
			byte byteData[] = md.digest();
			StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < byteData.length; i++) {
				String hex = Integer.toHexString(0xff & byteData[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		DbConnection d = new DbConnection();
//		d.queryResultSet("select * from usercredentials");
//		String picture = null;
//		String name = "bola";
//		String email = "johnsonseun15@gmail.com";
//		d.updateTable("UPDATE usercredentials SET profile_picture='" + picture + "', first_name='"
//		+ name + "' WHERE Email ='" + email + "'");
		d.md5Funct("password");
		System.out.println();
	}
}
