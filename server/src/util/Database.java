package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Database {
	private String name;
	private Connection connection;
	
	private static final int TIMEOUT = 30; // Number of seconds before a query times out.
	
	/**
	 * Start up a database connection with the given name.
	 * @param name
	 * @throws SQLException
	 */
	public Database(String name) {
		this.name = name;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
		} catch (SQLException e) {
			Log.err("Database connection could not be established", e);
			connection = null;
		}
	}
	
	/**
	 * Check if the table has already been setup.
	 * @return if successful
	 */
	public boolean exists() {
		if (connection != null) {
			try {
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(TIMEOUT);
				
				ResultSet rs = statement.executeQuery("SELECT count(*) AS count FROM sqlite_master WHERE type='table' AND name='properties'");
				return rs.getInt("count") > 0;
			} catch (SQLException e) {
				Log.err("SQL exception when checking if table exists", e);
			}
		}
		
		return false;
	}
	
	/**
	 * Create the table, dropping any existing table.
	 * @return
	 */
	public boolean create() {		
		if (connection != null) {
			try {
		          Statement statement = connection.createStatement();
		          statement.setQueryTimeout(TIMEOUT);
		          
		          statement.executeUpdate("DROP TABLE IF EXISTS properties");
		          statement.executeUpdate("CREATE TABLE properties ("
		          		+ "id INTEGER NOT NULL, "
		          		+ "key TEXT NOT NULL, "
		          		+ "data TEXT NOT NULL, "
		          		+ "PRIMARY KEY (id, key))");
		          return true;
			} catch (SQLException e) {
				Log.err("SQL exception when creating table", e);
			}
		}
		
		return false;
	}
	
	/**
	 * Set the data of a specific ID and key.
	 * @param id
	 * @param key
	 * @param data
	 * @return if successful
	 */
	public boolean add(int id, String key, String data) {
		if (connection != null) {
			try {
				PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO properties (id, key, data) VALUES(?,?,?)");
				statement.setQueryTimeout(TIMEOUT);
	
				statement.setInt(1, id);
				statement.setString(2, key);
				statement.setString(3, data);
				statement.executeUpdate();
				
				return true;
			} catch (SQLException e) {
				Log.err("SQL exception when creating table", e);
			}
		}
		
		return false;
	}

	/**
	 * Get all the data of a given ID.
	 * @param id
	 * @return HashMap<key, data>
	 */
	public HashMap<String, String> getAll(int id) {
		if (connection != null) {
			try {
				PreparedStatement statement = connection.prepareStatement("SELECT key, data FROM properties WHERE id = ?");
				statement.setQueryTimeout(TIMEOUT);
	
				statement.setInt(1, id);
				statement.execute();
				
				ResultSet results = statement.getResultSet();
				
				HashMap<String, String> data = new HashMap<>();
				while(results.next()) {
					data.put(results.getString("key"), results.getString("data"));
				}
				
				return data;
			} catch (SQLException e) {
				// Error 0 means no results were found, can be ignored.
				if (e.getErrorCode() != 0) {
					Log.err("SQL exception when selecting values", e);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Get the data at given a specific ID and key.
	 * @param id
	 * @param key
	 * @return String data
	 */
	public String get(int id, String key) {
		if (connection != null) {
			try {
				PreparedStatement statement = connection.prepareStatement("SELECT data FROM properties WHERE id = ? AND key = ?");
				statement.setQueryTimeout(TIMEOUT);
	
				statement.setInt(1, id);
				statement.setString(2, key);
				statement.execute();
				
				ResultSet result = statement.getResultSet();
				
				return result.getString("data");
			} catch (SQLException e) {
				// Error 0 means no results were found, can be ignored.
				if (e.getErrorCode() != 0) {
					Log.err("SQL exception when selecting value", e);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Get the name of the database.
	 * @return String name
	 */
	public String getName() {
		return name;
	}
	
	public static void main(String[] args) {
		Database db = new Database("test");
		System.out.println(db.create());
		db.add(1, "test", "NULL");
		db.add(2, "aaa", "no");
		db.add(2, "aaa", "test");
		System.out.println(db.getAll(2));
	}
	
}
