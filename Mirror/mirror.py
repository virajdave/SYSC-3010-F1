from tkinter import *
from time import *
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
		self.smallFontSize = 20

		# Base GUI needs
		#
		self.timeVar = StringVar()
		self.dateVar = StringVar()
		self.tempVar = StringVar()
		self.conditionsVar = StringVar()
		self.minmaxVar = StringVar()

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
			time.sleep(0.1)

		
	# GUI ######################################################################################################
	def createWidgets(self):
		self.top.configure(background=self.mirrorBg)
		w, h = self.top.winfo_screenwidth(), self.top.winfo_screenheight()
		self.top.overrideredirect(1)
		self.top.geometry("%dx%d+0+0" % (w, h))

		# time section of gui
		self.timeFrame = Frame(self.top, bg=self.mirrorBg)
		self.timeFrame.place(rely=0.0, relx=0.0, x=0, y=0, anchor=NW)
		self.timeLab = Label(self.timeFrame, textvariable=self.timeVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.largeFontSize)) \
		.grid(row=0, column=0)
		self.dateLab = Label(self.timeFrame, textvariable=self.dateVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.smallFontSize)) \
		.grid(row=1, column=0)


		# weather section of gui
		self.weatherFrame = Frame(self.top, bg=self.mirrorBg)
		self.weatherFrame.place(rely=0.0, relx=1.0, x=0, y=0, anchor=NE)
		self.tempLab = Label(self.weatherFrame, textvariable=self.tempVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.largeFontSize)) \
		.grid(row=0, column=0)
		self.conLab = Label(self.weatherFrame, textvariable=self.conditionsVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.smallFontSize)) \
		.grid(row=1, column=0)
		self.minmaxLab = Label(self.weatherFrame, textvariable=self.minmaxVar, fg=self.mirrorFg, bg=self.mirrorBg, font=(self.fontType, self.smallFontSize)) \
		.grid(row=2, column=0)

		# insert logo
		self.canvas_image = PhotoImage(file='CAM.png')
		# Resizing
		self.canvas_image = self.canvas_image.subsample(3, 3)
		self.logo = Label(self.top, image=self.canvas_image, bg=self.mirrorBg).place(rely=1.0, relx=0.0, x=0, y=0, anchor=SW)

	def showGUI(self):
		self.top.mainloop()
		