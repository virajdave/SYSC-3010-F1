package util;

import util.Database;

public class DatabaseStub extends Database {

	public DatabaseStub() {
		super("null");
	}
	
	@Override
	public void open() {
		connection = null;
	}

}
