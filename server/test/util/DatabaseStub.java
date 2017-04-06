package util;

import util.Database;

public class DatabaseStub extends Database {

	public DatabaseStub() {
		super("null");
	}
	
	@Override
	public boolean open() {
		connection = null;
		return true;
	}

}
