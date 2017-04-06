package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private String name;
	private Connection connection;
	
	private static final int TIMEOUT = 30; // Number of seconds before a query times out.
	
	public Database(String name) throws SQLException {
		this.name = name;
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
	}
	
	public boolean exists() {
		try {
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(TIMEOUT);
			
			ResultSet rs = statement.executeQuery("SELECT count(*) AS count FROM sqlite_master WHERE type='table' AND name='properties'");
			return rs.getInt("count") > 0;
		} catch (SQLException e) {
			Log.err("SQL exception when checking if table exists", e);
		}
		return false;
	}
	
	public boolean create() {
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
		return false;
	}
	
	public boolean add(int id, String key, String data) {
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
		
		return false;
	}

	public boolean getAll(int id) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT key, data FROM properties WHERE id = ?");
			statement.setQueryTimeout(TIMEOUT);

			statement.setInt(1, id);
			statement.execute();
			
			ResultSet s = statement.getResultSet();
			System.out.println("-----");
			while(s.next()) {
				System.out.println(s.getString("key"));
			}
			
			return true;
		} catch (SQLException e) {
			Log.err("SQL exception when selecting values", e);
		}
		
		return false;
	}
	
	public String get(int id, String key) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT data FROM properties WHERE id = ? AND key = ?");
			statement.setQueryTimeout(TIMEOUT);

			statement.setInt(1, id);
			statement.setString(2, key);
			statement.execute();
			
			ResultSet s = statement.getResultSet();			
			return s.getString("data");
		} catch (SQLException e) {
			Log.err("SQL exception when selecting value", e);
		}
		
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public static void main(String[] args) {
		try {
			Database db = new Database("test");
			System.out.println(db.create());
			db.add(2, "test", "data");
			db.add(2, "aaa", "no");
			db.add(2, "aaa", "test");
			db.getAll(2);
			db.getAll(3);
			System.out.println(db.get(2, "aaa"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
