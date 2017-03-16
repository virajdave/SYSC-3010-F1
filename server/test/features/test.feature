Feature: Test
	Scenario: Start the manager
		Given the manager is started
		When a device named 'switch' of type '0' is connected
