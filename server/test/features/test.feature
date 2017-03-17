Feature: System Test
	Scenario: Turn on/off light from app
		Given the manager is started
		And a device named 'light1' of type '0' is connected
		Then the light 'light1' should be turned 'off'
		
		When I turn 'off' the light 'light1' from the app
		Then the light 'light1' should be turned 'off'
		
		When I turn 'on' the light 'light1' from the app
		Then the light 'light1' should be turned 'on'

		When I turn 'off' the light 'light1' from the app
		Then the light 'light1' should be turned 'off'
		
	Scenario: Turn on/off switch
		Given the manager is started
		And a device named 'switch1' of type '1' is connected
		Then the switch 'switch1' should be turned 'off'
		
		When I turn 'off' the switch 'switch1'
		Then the switch 'switch1' should be turned 'off'
		
		When I turn 'on' the switch 'switch1'
		Then the switch 'switch1' should be turned 'on'
		
		When I turn 'on' the switch 'switch1' from the app
		Then the switch 'switch1' should be turned 'on'
		
		When I turn 'off' the switch 'switch1' from the app
		Then the switch 'switch1' should be turned 'off'