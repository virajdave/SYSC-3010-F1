Feature: App Test 
Scenario: Get NETINFO on app 
	Given the manager is started 
	And a 'mirror' named 'mir' is connected 
	And a 'thermostat' named 'thermo' is connected 
	And a 'thermostat' named 'thermo2' is connected 
	
	Then I should see the following list of devices in the app: 
		| mir     |
		| thermo  |
		| thermo2 |

Scenario: Get DEVINFO on app 
	Given the manager is started 
	And a 'light' named 'light1' is connected 
	
	Then I should see the following list of devices in the app: 
		| light1 | false |
	Then the light 'light1' should be: 
		| set   | off   |
		| dead  | false |
