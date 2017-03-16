package stepDefinitions;

import static org.junit.Assert.*;
import cucumber.api.java.en.*;
import main.*;

public class Steps {
	Manager manager;

	@Given("^the manager is started$")
	public void startManager() throws Throwable {
		manager = new Manager(new Server(null));
	}
}
