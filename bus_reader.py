#!/usr/bin/python
import time
import os

from time import sleep
from subprocess import call
from Adafruit_CharLCDPlate import Adafruit_CharLCDPlate

# Initialize the LCD plate.  Should auto-detect correct I2C bus.  If not,
# pass '0' for early 256 MB Model B boards or '1' for all later versions
lcd = Adafruit_CharLCDPlate()

# Clear display and show greeting, pause 1 sec
lcd.clear()
lcd.message("Bus number reader")
sleep(1)


filename = "/home/pi/bus_number.txt"
killCommand = ["/home/pi/bus-number/killnode.sh"]
nodeCommand = "node /home/pi/bus-number/bus.js "
broadcastNumberText = "\nBroadcasting "

debounceTime = 100

f = open(filename, 'r')
line = f.readline()
f.close()



busNumber = int(line)
broadcastingNumber = busNumber;

print busNumber

def getTime():
  return int(round(time.time() * 1000))

def shouldIProcessThisPress():
  global previousPressedTime
  currentTime = getTime()
  timeDifference = currentTime - previousPressedTime
  if timeDifference > debounceTime:
    previousPressedTime = currentTime
    return True
  else:
    return False



def saveNumberToFile(number):
  f = open(filename, 'w')
  numString = str(number)
  f.write(numString)
  f.close()




def restartNodeWithThisNumber(number):
  global broadcastingNumber
  broadcastingNumber = number
  commandToRun = nodeCommand + str(number) + " &"
  print "kill"
  call(killCommand)
  print "node command " + commandToRun
  os.system(commandToRun)

  refreshLCD()

def shutdown():
  lcd.clear()
  os.system("poweroff")

def refreshLCD():
  textToShow = str(busNumber) + broadcastNumberText + str(broadcastingNumber)
  lcd.clear()
  lcd.message(textToShow)


previousPressedTime = getTime()
restartNodeWithThisNumber(busNumber)

while True:
    if lcd.buttonPressed(lcd.UP) and shouldIProcessThisPress():
      busNumber += 1
      refreshLCD()
      saveNumberToFile(busNumber)
    elif lcd.buttonPressed(lcd.DOWN) and shouldIProcessThisPress():
      busNumber -= 1
      refreshLCD()
      saveNumberToFile(busNumber)
    elif lcd.buttonPressed(lcd.RIGHT) and shouldIProcessThisPress():
      restartNodeWithThisNumber(busNumber)
    elif lcd.buttonPressed(lcd.SELECT) and shouldIProcessThisPress():
      shutdown()


  

