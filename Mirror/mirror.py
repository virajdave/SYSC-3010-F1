from tkinter import *
from time import *
from weather import *
import time

# DATA ###################################################################################################
# colours
mirrorBg = 'black'
mirrorFg = '#2E99A9'  # Match the logo color
# Font settings
fontType = "Helvetica"
largeFontSize = 80
smallFontSize = 20

# Base GUI needs
top = Tk()
timeVar = StringVar()
dateVar = StringVar()
tempVar = StringVar()
conditionsVar = StringVar()
minmaxVar = StringVar()

# GUI Functions ##########################################################################################

# Changes the Gui variables to adjust temperature data
def tempUpdate():
    weatherData = data_organizer(data_fetch(url_builder('Ottawa,Ca')))
    temp = str(weatherData['temp']) + ' °C '
    tempVar.set(temp)
    conditions = weatherData['city'] + ', ' + weatherData['country'] + "\t   " + weatherData['sky'] + "    "
    conditionsVar.set(conditions)
    minmax = 'Max Temp: ' + str(weatherData['temp_max']) + '\t' + 'Min Temp: ' + str(weatherData['temp_min'])
    minmaxVar.set(minmax)


# Changes GUI Variable to update the time based on system time
def timeUpdate(time):
    timeVar.set(time)

# Changes GUI Variables to update the date under the time
def dateUpdate(date):
    dateVar.set(date)


# Constantly runs to update GUI options
def update():
	timeUpdate()
	tempUpdate()
	top.update_idletasks()
	top.after(500, update)

# Reads the queue from the controller and updates as needed
def runnerLoop(queue):
	while True:
		if not queue.empty():
			while not queue.empty():
				message = queue.get()
				if message.messageType == 'time':
					timeUpdate(message.info)
				elif message.messageType == 'date':
					dateUpdate(message.info)
		time.sleep(0.1)

	
# GUI ######################################################################################################
def createmirrorGUI(guiRecvQueue):
	top.configure(background=mirrorBg)
	w, h = top.winfo_screenwidth(), top.winfo_screenheight()
	top.overrideredirect(1)
	top.geometry("%dx%d+0+0" % (w, h))

	# time section of gui
	timeFrame = Frame(top, bg=mirrorBg)
	timeFrame.place(rely=0.0, relx=0.0, x=0, y=0, anchor=NW)
	timeLab = Label(timeFrame, textvariable=timeVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, largeFontSize)) \
	.grid(row=0, column=0)
	dateLab = Label(timeFrame, textvariable=dateVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, smallFontSize)) \
	.grid(row=1, column=0)


	# weather section of gui
	weatherFrame = Frame(top, bg=mirrorBg)
	weatherFrame.place(rely=0.0, relx=1.0, x=0, y=0, anchor=NE)
	tempLab = Label(weatherFrame, textvariable=tempVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, largeFontSize)) \
	.grid(row=0, column=0)
	conLab = Label(weatherFrame, textvariable=conditionsVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, smallFontSize)) \
	.grid(row=1, column=0)
	minmaxLab = Label(weatherFrame, textvariable=minmaxVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, smallFontSize)) \
	.grid(row=2, column=0)

	# insert logo
	canvas_image = PhotoImage(file='CAM.png')
	# Resizing
	canvas_image = canvas_image.subsample(3, 3)
	logo = Label(top, image=canvas_image, bg=mirrorBg).place(rely=1.0, relx=0.0, x=0, y=0, anchor=SW)
	
	top.mainloop()
	