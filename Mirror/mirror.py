from tkinter import *
from time import *
from array import *
import time


class mirrorGUI:
	
	def __init__(self, root, guiRecvQueue):
		self.top = root
		# DATA ###################################################################################################
		# colours
		self.mirrorBg = 'black'
		self.mirrorFg = '#2E99A9'  # Match the logo color
		# Font settings
		self.fontType = "Helvetica"
		self.largeFontSize = 80
		self.medFontSize = 25
		self.smallFontSize = 20

		# Base GUI needs
		#
		self.timeVar = StringVar()
		self.dateVar = StringVar()
		self.tempVar = StringVar()
		self.conditionsVar = StringVar()
		self.minmaxVar = StringVar()
		self.busStation = StringVar()
		self.firstDirectionTitle = StringVar()
		self.firstDirectionTimes = StringVar()
		self.secondDirectionTitle = StringVar()
		self.secondDirectionTimes = StringVar()
		self.widgets= []
		self.createWidgets()
		
		
	# GUI widget display Updators #######################################################################################

	# Changes the Gui variable to adjust temperature data
	def tempUpdate(self, weatherData):
		temp = str(weatherData['temp']) + ' °C '
		self.tempVar.set(temp)
	
	# Changes the Gui variable to adjust conditions data
	def conditionsUpdate(self, weatherData):
		conditions = weatherData['city'] + ', ' + weatherData['country'] + "\t   " + weatherData['sky'] + "    "
		self.conditionsVar.set(conditions)
	
	# Changes the Gui variable to adjust min and max data
	def minMaxlineUpdate(self, weatherData):
		minmax = 'Max Temp: ' + str(weatherData['temp_max']) + '\t' + 'Min Temp: ' + str(weatherData['temp_min'])
		self.minmaxVar.set(minmax)
		
	# Changes GUI Variable to update the time based on system time
	def timeUpdate(self, time):
		self.timeVar.set(time)

	# Changes GUI Variables to update the date under the time
	def dateUpdate(self, date):
		self.dateVar.set(date)

	def busUpdate (self, busInfo):
		self.busStation.set("Station: " + busInfo['station'] )
		self.firstDirectionTitle.set("Direction: " + busInfo['direction1']['dest'])
		directionOneTimes = ( busInfo['direction1']['firstBus'] + '\n'  +
											busInfo['direction1']['secondBus'] + '\n'  +
											busInfo['direction1']['thirdBus'] )
		self.firstDirectionTimes.set(directionOneTimes)
		
		self.secondDirectionTitle.set("Direction: " + busInfo['direction2']['dest'])
		directionTwoTimes = ( busInfo['direction2']['firstBus'] + '\n'  +
											busInfo['direction2']['secondBus'] + '\n'  +
											busInfo['direction2']['thirdBus'] )
		self.secondDirectionTimes.set(directionTwoTimes)
		
		
	# Customizers ###########################################################################
	def changeColour(self, colour):
		self.logoLab.configure(bg=colour) 		#Special case the logo
		for w in self.widgets:
			w.configure(fg=colour)
	
	# GUI communication to controller #####################################################################################
	
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
						self.minMaxlineUpdate(message.info)
					elif message.messageType == 'bus':
						self.busUpdate(message.info)
			time.sleep(0.1)

		
	# GUI ######################################################################################################
	def createWidgets(self):
		self.top.configure(background=self.mirrorBg)
		w, h = self.top.winfo_screenwidth(), self.top.winfo_screenheight()
		self.top.overrideredirect(1)
		self.top.geometry("%dx%d+0+0" % (w, h))
		self.setUpTimeWidget()		
		self.setUpWeatherWidget()
		self.setUpBusWidget()
		self.setUpLogoWidget()
		
		
	#Creates and places the top left hand of GUI which is time
	def setUpTimeWidget(self):
		self.timeFrame = Frame(self.top, bg=self.mirrorBg)
		self.timeLab = Label(self.timeFrame, textvariable=self.timeVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.largeFontSize)) 
		self.widgets.append(self.timeLab)
		self.dateLab = Label(self.timeFrame, textvariable=self.dateVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.smallFontSize)) 
		self.widgets.append(self.dateLab)
		
		#Positioning of widgets
		self.timeFrame.place(rely=0.0, relx=0.0, x=0, y=0, anchor=NW)
		self.timeLab.grid(row=0, column=0)
		self.dateLab.grid(row=1, column=0)
	
	
	#Creates and places the top right hand of GUI which is weather	
	def setUpWeatherWidget(self):
		self.weatherFrame = Frame(self.top, bg=self.mirrorBg)
		self.tempLab = Label(self.weatherFrame, textvariable=self.tempVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.largeFontSize)) 
		self.widgets.append(self.tempLab)
		self.conLab = Label(self.weatherFrame, textvariable=self.conditionsVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.smallFontSize)) 
		self.widgets.append(self.conLab)
		self.minmaxLab = Label(self.weatherFrame, textvariable=self.minmaxVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.smallFontSize)) 
		self.widgets.append(self.minmaxLab)
		
		#Positioning of widgets
		self.weatherFrame.place(rely=0.0, relx=1.0, x=0, y=0, anchor=NE)
		self.tempLab.grid(row=0, column=0)
		self.conLab.grid(row=1, column=0)
		self.minmaxLab.grid(row=2, column=0)
	
	
	#Creates and places the Bottom right hand of GUI which is bus info	
	def setUpBusWidget(self):
		self.busFrame = Frame(self.top, bg=self.mirrorBg)
		self.busStationLabel = Label(self.busFrame, textvariable=self.busStation, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.medFontSize), justify = LEFT) 
		self.widgets.append(self.busStationLabel)
		self.firstDirectionTitleLab = Label(self.busFrame, textvariable=self.firstDirectionTitle, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.medFontSize)) 
		self.widgets.append(self.firstDirectionTitleLab)
		self.firstDirectionTimeLabel = Label(self.busFrame, textvariable=self.firstDirectionTimes, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.smallFontSize), justify=RIGHT) 
		self.widgets.append(self.firstDirectionTimeLabel)
		
		self.secondDirectionTitleLab = Label(self.busFrame, textvariable=self.secondDirectionTitle, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.medFontSize)) 
		self.widgets.append(self.secondDirectionTitleLab)
		self.secondDirectionTimeLabel = Label(self.busFrame, textvariable=self.secondDirectionTimes, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.smallFontSize), justify=RIGHT) 
		self.widgets.append(self.secondDirectionTimeLabel)
		
		#Positioning of widgets
		self.busFrame.place(rely=1.0, relx=1.0, x=0, y=0, anchor=SE)
		self.busStationLabel.grid(row=0, column=0, sticky = W)
		self.firstDirectionTitleLab.grid(row=1, column=0, sticky = W)
		self.firstDirectionTimeLabel.grid(row=2, column=0, sticky = E)
		self.secondDirectionTitleLab.grid(row=3, column=0, sticky = W)
		self.secondDirectionTimeLabel.grid(row=4, column=0, sticky = E)
	
	
	#Creates and places the Bottom left hand of GUI which is Logo
	def setUpLogoWidget(self):
		self.canvas_image = PhotoImage(file='CAM.png')
		self.logoLab = Label(self.top, image=self.canvas_image, bg=self.mirrorFg, borderwidth=0)
		
		#Positioning of widgets
		self.logoLab.place(rely=1.0, relx=0.0, x=0, y=0, anchor=SW)
		
	
	def showGUI(self):
		self.top.mainloop()
		