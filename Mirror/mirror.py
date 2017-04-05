#==============================================================================
#title          		:mirror.py
#description     		:GUI for the Smart Mirror
#author          		:Dillon Verhaeghe
#date            		:20170326
#version         		:0.4
#usage           		:python3 mirrorRunner.py 10.0.0.1 3010
#notes           		:Should only be invoked from the mirrorController script
#python_version :3 
#==============================================================================

from tkinter import *
from time import *
from array import *
import time

class mirrorGUI:
	
	def __init__(self, root, guiRecvQueue):
                self.top = root
		# DATA ###########################################################################
		# colours
                self.mirrorBg = 'black'
                self.mirrorFg = '#2E99A9'  # Match the logo color
		# Font settings
                self.fontType = "Helvetica"
                self.largeFontSize = 80
                self.medFontSize = 25
                self.smallFontSize = 20
                self.direction = 0
		# Base GUI needs
		#
                self.timeVar = StringVar()
                self.dateVar = StringVar()
                self.tempVar = StringVar()
                self.conditionsVar = StringVar()
                self.thermoVar = StringVar()
                self.busStation = StringVar()
                self.firstDirectionTitle = StringVar()
                self.firstDirectionTimes = StringVar()
                self.secondDirectionTitle = StringVar()
                self.secondDirectionTimes = StringVar()
                self.widgets= []
                self.createWidgets()
		
		
	# GUI widget display Updators ########################################################

	# Changes the Gui variable to adjust temperature data
	def tempUpdate(self, weatherData):
		temp = str(weatherData['temp']) + ' °C '
		self.tempVar.set(temp)
	
	# Changes the Gui variable to adjust conditions data
	def conditionsUpdate(self, weatherData):
		conditions = weatherData['city'] + ', ' + weatherData['country'] + "\t   " + weatherData['sky'] + "    "
		self.conditionsVar.set(conditions)
		
	
	# Changes the Gui variable to adjust min and max data
	def thermoUpdate(self, temperature):
		thermo = 'House Temperature: ' +  str (temperature) + ' °C '
		#minmax = 'Max Temp: ' + str(weatherData['temp_max']) + '\t' + 'Min Temp: ' + str(weatherData['temp_min'])
		self.thermoVar.set(thermo)
		
	# Changes GUI Variable to update the time based on system time
	def timeUpdate(self, time):
		self.timeVar.set(time)

	# Changes GUI Variables to update the date under the time
	def dateUpdate(self, date):
		self.dateVar.set(date)
	
	# Changes the gui to display bus info that could have anywhere from 0 to 3 trip times
	def busUpdate (self, busInfo):
                self.busStation.set("Station: " + busInfo['station'] )
                if(self.direction == 1 or self.direction == 0 and 'direction1' in busInfo):
                        self.firstDirectionTitle.set(busInfo['direction1']['dest'])
                        directionOneTimes = ""
                        if 'busTime0' in busInfo['direction1']:
                                directionOneTimes +=   busInfo['direction1']['busTime0'] + '\n'
                        if 'busTime1' in busInfo['direction1']:
                                directionOneTimes +=   busInfo['direction1']['busTime1'] + '\n'
                        if 'busTime2' in busInfo['direction1']:
                                directionOneTimes +=   busInfo['direction1']['busTime2'] + '\n'
                        self.firstDirectionTimes.set(directionOneTimes)
                else:
                        self.firstDirectionTitle.set("")
                        self.firstDirectionTimes.set("")
                if (self.direction == 2 or self.direction == 0 and 'direction2' in busInfo):
                        self.secondDirectionTitle.set(busInfo['direction2']['dest'])
                        directionTwoTimes = ""
                        if 'busTime0' in busInfo['direction2']:
                                directionTwoTimes +=   busInfo['direction2']['busTime0'] + '\n'
                        if 'busTime1' in busInfo['direction2']:
                                directionTwoTimes +=   busInfo['direction2']['busTime1'] + '\n'
                        if 'busTime2' in busInfo['direction2']:
                                directionTwoTimes +=   busInfo['direction2']['busTime2'] + '\n'
                        self.secondDirectionTimes.set(directionTwoTimes)
                else:
                        self.secondDirectionTitle.set("")
                        self.secondDirectionTimes.set("")
		
		
	# Customizers ########################################################################
	def changeColour(self, colour):
		self.logoLab.configure(bg=colour) 		#Special case the logo
		for w in self.widgets:
			w.configure(fg=colour)
	
		
	# GUI communication to controller ####################################################
	
	# Reads the queue from the controller and updates as needed
	def runnerLoop(self, queue):
		while True:
			if not queue.empty():
				while not queue.empty():
					message = queue.get()
					if message.messageType == 'time':
						self.timeUpdate(message.info)
					elif message.messageType == 'date':
						self.dateUpdate(message.info)
					elif message.messageType == 'weather':
						self.tempUpdate(message.info)
						self.conditionsUpdate(message.info)
					elif message.messageType == 'bus':
						self.busUpdate(message.info)
					elif message.messageType == 'colour':
						self.changeColour(message.info)
					elif message.messageType == 'direction':
                        self.direction = int(message.info)
					elif message.messageType == 'thermo':
						self.thermoUpdate(message.info)
			time.sleep(0.1)

			
		
	# GUI ################################################################################
	
	#main gui creator
	def createWidgets(self):
		self.top.configure(background=self.mirrorBg)
	#	w, h = self.top.winfo_screenwidth(), self.top.winfo_screenheight()
	#	self.top.overrideredirect(1)
	#	self.top.geometry("%dx%d+0+0" % (w, h))
		self.setUpTimeWidget()		
		self.setUpWeatherWidget()
		self.setUpBusWidget()
		self.setUpLogoWidget()
		
		
	#Creates and places the top left hand of GUI which is time
	def setUpTimeWidget(self):
		self.timeFrame = Frame(self.top, bg=self.mirrorBg)
		self.timeLab = Label(self.timeFrame,
							textvariable = self.timeVar,
							fg=self.mirrorFg,
							bg=self.mirrorBg,
							font=(self.fontType, self.largeFontSize)) 
		self.widgets.append(self.timeLab)
		
		self.dateLab = Label(self.timeFrame,
							textvariable=self.dateVar,
							fg=self.mirrorFg,
							bg=self.mirrorBg,
							font=(self.fontType, self.smallFontSize)) 
		self.widgets.append(self.dateLab)
		
		#Positioning of widgets
		self.timeFrame.place(rely=0.0, relx=0.0, x=0, y=0, anchor=NW)
		self.timeLab.grid(row=0, column=0)
		self.dateLab.grid(row=1, column=0)

		
	#Creates and places the top right hand of GUI which is weather	
	def setUpWeatherWidget(self):
		self.weatherFrame = Frame(self.top, bg=self.mirrorBg)
		self.tempLab = Label(self.weatherFrame,
							textvariable=self.tempVar,
							fg=self.mirrorFg,
							bg=self.mirrorBg,
							font=(self.fontType, self.largeFontSize)) 
		self.widgets.append(self.tempLab)
		
		self.conLab = Label(self.weatherFrame,
							textvariable=self.conditionsVar,
							fg=self.mirrorFg,
							bg=self.mirrorBg,
							font=(self.fontType, self.smallFontSize)) 
		self.widgets.append(self.conLab)
		
		self.thermoLab = Label(self.weatherFrame,
								textvariable=self.thermoVar,
								fg=self.mirrorFg,
								bg=self.mirrorBg,
								font=(self.fontType, self.smallFontSize)) 
		self.widgets.append(self.thermoLab)
		
		#Positioning of widgets
		self.weatherFrame.place(rely=0.0, relx=1.0, x=0, y=0, anchor=NE)
		self.tempLab.grid(row=0, column=0)
		self.conLab.grid(row=1, column=0)
		self.thermoLab.grid(row=2, column=0)
	
	
	#Creates and places the Bottom right hand of GUI which is bus info	
	def setUpBusWidget(self):
		self.busFrame = Frame(self.top, bg=self.mirrorBg)
		self.busStationLabel = Label(self.busFrame,
									textvariable=self.busStation,
									fg=self.mirrorFg,
									bg=self.mirrorBg,
									font=(self.fontType, self.medFontSize),
									justify = LEFT) 
		self.widgets.append(self.busStationLabel)
		
		self.firstDirectionTitleLab = Label(self.busFrame,
											textvariable=self.firstDirectionTitle,
											fg=self.mirrorFg,
											bg=self.mirrorBg,
											font=(self.fontType, self.medFontSize)) 
		self.widgets.append(self.firstDirectionTitleLab)
		
		self.firstDirectionTimeLabel = Label(self.busFrame,
											textvariable=self.firstDirectionTimes,
											fg=self.mirrorFg,
											bg=self.mirrorBg,
											font=(self.fontType, self.smallFontSize),
											justify=RIGHT) 
		self.widgets.append(self.firstDirectionTimeLabel)
		
		self.secondDirectionTitleLab = Label(self.busFrame,
											textvariable=self.secondDirectionTitle,
											fg=self.mirrorFg,
											bg=self.mirrorBg,
											font=(self.fontType, self.medFontSize)) 
		self.widgets.append(self.secondDirectionTitleLab)
		self.secondDirectionTimeLabel = Label(self.busFrame,
											textvariable=self.secondDirectionTimes,
											fg=self.mirrorFg,
											bg=self.mirrorBg,
											font=(self.fontType, self.smallFontSize),
											justify=RIGHT) 
		self.widgets.append(self.secondDirectionTimeLabel)
		
		#Positioning of widgets
		self.busFrame.place(rely=1.0, relx=1.0, x=0, y=0, anchor=SE)
		self.busStationLabel.grid(row=0, column=0, sticky = W)
		self.firstDirectionTitleLab.grid(row=1, column=0, sticky = W)
		self.firstDirectionTimeLabel.grid(row=2, column=0, sticky = E)
		self.secondDirectionTitleLab.grid(row=3, column=0, sticky = W)
		self.secondDirectionTimeLabel.grid(row=4, column=0, sticky = E)
	
	
	# Creates and places the Bottom left hand of GUI which is Logo
	def setUpLogoWidget(self):
		self.canvas_image = PhotoImage(file='logo/CAM.png')
		self.logoLab = Label(self.top,
							image=self.canvas_image,
							bg=self.mirrorFg,
							borderwidth=0)
		
		#Positioning of widgets
		self.logoLab.place(rely=1.0, relx=0.0, x=0, y=0, anchor=SW)
		
	# Displays the gui
	def showGUI(self):
		self.top.mainloop()
		
