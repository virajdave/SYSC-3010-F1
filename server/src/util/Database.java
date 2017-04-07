package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;

public class Database {
	private String name;
	protected Connection connection;
	
	private static final int TIMEOUT = 30; // Number of seconds before a query times out.
	
	/**
	 * Start up a database connection with the given name.
	 * @param name
	 * @throws SQLException
	 */
	public Database(String name) {
		this.name = name;
		open();
	}
	
	/**
	 * Open the database.
	 */
	public boolean open() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
			return true;
		} catch (SQLException e) {
			Log.err("Database connection could not be established", e);
			connection = null;
		}
		return false;
	}
	
	/**
	 * Close the database.
	 * @return if successful
	 */
	public boolean close() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				Log.err("Database could not be closed", e);
			}
		}
		
		return false;
	}
	
	/**
	 * Check if the table has already been setup.
	 * @return if successful
	 */
	public boolean exists() {
		if (connection != null) {
			try (Statement statement = connection.createStatement()) {
				statement.setQueryTimeout(TIMEOUT);
				
				try (ResultSet rs = statement.executeQuery("SELECT count(*) AS count FROM sqlite_master WHERE type='table' AND name='properties'")) {
					return rs.getInt("count") > 0;
				}
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
			try (Statement statement = connection.createStatement()) {
		          statement.setQueryTimeout(TIMEOUT);
		          
		          statement.executeUpdate("DROP TABLE IF EXISTS devices");
		          statement.executeUpdate("CREATE TABLE devices ("
		          		+ "id INTEGER NOT NULL PRIMARY KEY, "
		          		+ "type INTEGER NOT NULL, "
		          		+ "inet TEXT NOT NULL)");
		          
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
	 * Add the entry of a specific device ID.
	 * @param id
	 * @param key
	 * @param data
	 * @return if successful
	 */
	public boolean addDevice(int id, int type, String inet) {
		if (connection != null) {
			try (PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO devices (id, type, inet) VALUES(?,?,?)")) {
				statement.setQueryTimeout(TIMEOUT);
	
				statement.setInt(1, id);
				statement.setInt(2, type);
				statement.setString(3, inet);
				statement.executeUpdate();
				
				return true;
			} catch (SQLException e) {
				Log.err("SQL exception when adding device entry", e);
			}
		}
		
		return false;
	}
	
	/**
	 * Delete the entry of a specific device ID.
	 * @param id
	 * @param key
	 * @param data
	 * @return if successful
	 */
	public boolean removeDevice(int id) {
		if (connection != null) {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM devices WHERE id = ?")) {
				statement.setQueryTimeout(TIMEOUT);
				
				statement.setInt(1, id);
				statement.executeUpdate();
				
				statement.close();
				
				try (PreparedStatement statement2 = connection.prepareStatement("DELETE FROM properties WHERE id = ?")) {
					statement2.setInt(1, id);
					statement2.executeUpdate();
				}
				
				return true;
			} catch (SQLException e) {
				Log.err("SQL exception when removing device entry", e);
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
	public boolean addProp(int id, String key, String data) {
		if (connection != null) {
			try (PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO properties (id, key, data) VALUES(?,?,?)")) {
				statement.setQueryTimeout(TIMEOUT);
	
				statement.setInt(1, id);
				statement.setString(2, key);
				statement.setString(3, data);
				statement.executeUpdate();
				
				return true;
			} catch (SQLException e) {
				Log.err("SQL exception when adding property entry", e);
			}
		}
		
		return false;
	}

	public HashMap<Integer, Entry<Integer, String>> getDevices() {
		if (connection != null) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT id, type, inet FROM devices")) {
				statement.setQueryTimeout(TIMEOUT);
	
				statement.execute();
				
				try (ResultSet results = statement.getResultSet()) {
					HashMap<Integer, Entry<Integer, String>> data = new HashMap<>();
					while(results.next()) {
						data.put(results.getInt("id"), new AbstractMap.SimpleEntry<Integer, String>(results.getInt("type"), results.getString("inet")));
					}
					
					return data;
				}
			} catch (SQLException e) {
				// Error 0 means no results were found, can be ignored.
				if (e.getErrorCode() != 0) {
					Log.err("SQL exception when selecting values", e);
				}
			}
		}
		
		return null;
	}

	public HashMap<Integer, HashMap<String, String>> getProp() {
		if (connection != null) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT id, key, data FROM properties")) {
				statement.setQueryTimeout(TIMEOUT);
	
				statement.execute();
				
				try (ResultSet results = statement.getResultSet()) {
					HashMap<Integer, HashMap<String, String>> data = new HashMap<>();
					while(results.next()) {
						HashMap<String, String> deviceData;
						int id = results.getInt("id");
						
						if (!data.containsKey(id)) {
							deviceData = new HashMap<>();
							data.put(id, deviceData);
						} else {
							deviceData = data.get(id);
						}
						
						deviceData.put(results.getString("key"), results.getString("data"));
					}
					
					return data;
				}
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
	 * Get all the data of a given ID.
	 * @param id
	 * @return HashMap<key, data>
	 */
	public HashMap<String, String> getProp(int id) {
		if (connection != null) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT key, data FROM properties WHERE id = ?")) {
				statement.setQueryTimeout(TIMEOUT);
	
				statement.setInt(1, id);
				statement.execute();
				
				try (ResultSet results = statement.getResultSet()) {
					HashMap<String, String> data = new HashMap<>();
					while(results.next()) {
						data.put(results.getString("key"), results.getString("data"));
					}
					
					return data;
				}
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
	public String getProp(int id, String key) {
		if (connection != null) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT data FROM properties WHERE id = ? AND key = ?")) {
				statement.setQueryTimeout(TIMEOUT);
	
				statement.setInt(1, id);
				statement.setString(2, key);
				statement.execute();
				
				try (ResultSet result = statement.getResultSet()) {
					return result.getString("data");
				}
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
		db.create();
		db.addDevice(2, 6, "test");
		db.addProp(2, "test", "here");
		db.addProp(3, "test", "aaaa");
		db.addProp(2, "go", "no,more");
		System.out.println(db.getDevices());
		System.out.println(db.getProp());
		db.removeDevice(2);
		System.out.println(db.getDevices());
		System.out.println(db.getProp());
	}
	
}
