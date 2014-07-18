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

debounceTime = 100

f = open(filename, 'r')
line = f.readline()
f.close()

previousPressedTime = None

busNumber = int(line)

print busNumber
lcd.message(line)


def shouldIProcessThisPress():
  global previousPressedTime
  currentTime = getTime()
  timeDifference = currentTime - previousPressedTime
  if timeDifference > debounceTime:
    previousPressedTime = currentTime
    return True
  else:
    return False

def getTime():
  return int(round(time.time() * 1000))

def saveNumberToFile(number):
  f = open(filename, 'w')
  numString = str(number)
  f.write(numString)
  f.close()

def restartNodeWithThisNumber(number):
  commandToRun = nodeCommand + str(number) + " &"
  print "kill"
  call(killCommand)
  print "node command " + commandToRun
  os.system(commandToRun)



previousPressedTime = getTime()

while True:
    if lcd.buttonPressed(lcd.UP) and shouldIProcessThisPress():
      busNumber += 1
      lcd.clear()
      lcd.message(busNumber)
      saveNumberToFile(busNumber)
    elif lcd.buttonPressed(lcd.DOWN) and shouldIProcessThisPress():
      busNumber -= 1
      lcd.clear()
      lcd.message(busNumber)
      saveNumberToFile(busNumber)
    elif lcd.buttonPressed(lcd.SELECT) and shouldIProcessThisPress():
      restartNodeWithThisNumber(busNumber)


  
