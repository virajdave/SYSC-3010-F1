class tempuratureSensor:
    temp = 30;
    def getTemp():
        return temp
   
    
class heater:
    on = False
    def onOff (self):
        if (self.h.on == True):
            self.h.on = False
            print ("Heater is now off")
        elif(self.h.on == False):
            self.h.on = True
            print ("Heater is now on")
class thermostatControl:
    ts = tempuratureSensor
    h = heater
    temp = 25
    currTemp = ts.temp
    def manageTemp(self):
        print ("currTemp = %f" % self.currTemp) 
        print ("wantedTemp = %d" % self.temp)
        n = 0
        if (self.currTemp < self.temp and self.h.on == False) :
            print ("Temp low activating heater")
            self.h.onOff(self)
            
      
        elif (self.currTemp-1 > self.temp and self.h.on == False) :
            print ("Temp high don't activate heater")          
        elif (self.currTemp-1 > self.temp and self.h.on) :
            print ("Temp high deactivating heater")
            self.h.onOff(self)
            
    def setNewTemp(self, t):
        self.Temp = t

tc = thermostatControl
tc.currTemp = 20
tc.manageTemp(tc)
tc.currTemp = 30
tc.manageTemp(tc)
tc.h.on = False
tc.manageTemp(tc)
