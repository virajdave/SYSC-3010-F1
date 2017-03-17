Feature: System Test 
Scenario: Turn on/off light from app 
	Given the manager is started 
	And a 'light' named 'light1' is connected 
	Then the light 'light1' should be: 
		| set   | off   |
		| dead  | false |
		
	When I turn 'off' the light 'light1' from the app 
	Then the light 'light1' should be turned 'off' 
	
	When I turn 'on' the light 'light1' from the app 
	Then the light 'light1' should be turned 'on' 
	
	When I turn 'off' the light 'light1' from the app 
	Then the light 'light1' should be turned 'off' 
	
Scenario: Turn on/off switch 
	Given the manager is started 
	And a 'switch' named 'switch1' is connected 
	Then the switch 'switch1' should be: 
		| set   | off   |
		| dead  | false |
		| light | null  |
		
	When I turn 'off' the switch 'switch1' 
	Then the switch 'switch1' should be turned 'off' 
	
	When I turn 'on' the switch 'switch1' 
	Then the switch 'switch1' should be turned 'on' 
	
	When I turn 'on' the switch 'switch1' from the app 
	Then the switch 'switch1' should be turned 'on' 
	
	When I turn 'off' the switch 'switch1' from the app 
	Then the switch 'switch1' should be turned 'off' 
	
Scenario: Light + Switch 
	Given the manager is started 
	And a 'light' named 'light1' is connected 
	And a 'light' named 'light2' is connected 
	And a 'switch' named 'switch1' is connected 
	
	When I connect light 'light1' the switch 'switch1' 
	Then the switch 'switch1' should be: 
		| set   | off    |
		| light | light1 |
		
	When I turn 'on' the light 'light1' from the app 
	Then the switch 'switch1' should be turned 'on' 
	
	When I turn 'off' the switch 'switch1' from the app 
	Then the switch 'switch1' should be turned 'off' 
	Then the light 'light1' should be turned 'off' 
	
	When I turn 'on' the light 'light2' from the app 
	When I connect light 'light2' the switch 'switch1' 
	Then the switch 'switch1' should be: 
		| set   | on     |
		| light | light2 |
		
	When I turn 'off' the switch 'switch1' 
	Then the switch 'switch1' should be turned 'off' 
	Then the light 'light2' should be turned 'off' 
