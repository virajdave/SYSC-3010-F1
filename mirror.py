from tkinter import *
from time import *
from weather import *

updateCounter = 500
# colours
mirrorBg = 'black'
mirrorFg = '#2E99A9'  # Match the logo color
# Font settings
fontType = "Helvetica"
largeFontSize = 80
smallFontSize = 20

def tempUpdate():
    global updateCounter
    if updateCounter > 500:
        weatherData = data_organizer(data_fetch(url_builder('Ottawa,Ca')))
        temp = str(weatherData['temp']) + ' °C '
        tempVar.set(temp)
        conditions = weatherData['city'] + ', ' + weatherData['country'] + "\t   " + weatherData['sky'] + "    "
        conditionsVar.set(conditions)
        minmax = 'Max Temp: ' + str(weatherData['temp_max']) + '\t' + 'Min Temp: ' + str(weatherData['temp_min'])
        minmaxVar.set(minmax)
        updateCounter = 0
    else:
        updateCounter += 1


def timeUpdate():
    hour = str(int(strftime("%I")))
    minute = strftime("%M")
    time = hour + ':' + minute
    timeVar.set(time)


def dateUpdate():
    dateVar.set(strftime("%B, %A %d %Y"))


# Constantly runs to update GUI options
def update():
    timeUpdate()
    tempUpdate()
    top.update_idletasks()
    top.after(500, update)


top = Tk()
top.configure(background=mirrorBg)
w, h = top.winfo_screenwidth(), top.winfo_screenheight()
top.overrideredirect(1)
top.geometry("%dx%d+0+0" % (w, h))

# time section of gui
timeFrame = Frame(top, bg=mirrorBg)
timeFrame.place(rely=0.0, relx=0.0, x=0, y=0, anchor=NW)
timeVar = StringVar()
timeUpdate()
timeLab = Label(timeFrame, textvariable=timeVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, largeFontSize)) \
    .grid(row=0, column=0)
dateVar = StringVar()
dateUpdate()
dateLab = Label(timeFrame, textvariable=dateVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, smallFontSize)) \
    .grid(row=1, column=0)


# weather section of gui
weatherFrame = Frame(top, bg=mirrorBg)
weatherFrame.place(rely=0.0, relx=1.0, x=0, y=0, anchor=NE)
tempVar = StringVar()
tempLab = Label(weatherFrame, textvariable=tempVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, largeFontSize)) \
    .grid(row=0, column=0)
conditionsVar = StringVar()
conLab = Label(weatherFrame, textvariable=conditionsVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, smallFontSize)) \
    .grid(row=1, column=0)
minmaxVar = StringVar()
minmaxLab = Label(weatherFrame, textvariable=minmaxVar, fg=mirrorFg, bg=mirrorBg, font=(fontType, smallFontSize)) \
    .grid(row=2, column=0)
tempUpdate()

# insert logo
canvas_image = PhotoImage(file='M:\Sysc3010\CAM.png')
# Resizing
canvas_image = canvas_image.subsample(3, 3)
logo = Label(top, image=canvas_image, bg=mirrorBg).place(rely=1.0, relx=0.0, x=0, y=0, anchor=SW)


top.after(500, update)
top.mainloop()
