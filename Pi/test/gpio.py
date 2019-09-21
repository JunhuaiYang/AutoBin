#!/usr/bin/python
import RPi.GPIO as GPIO
import sys
import time
LED = 23

def main():
    GPIO.setwarnings(False)
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(LED,GPIO.OUT)
    while (True):
        GPIO.output(LED,True)
        time.sleep(0.5)
        GPIO.output(LED,False)        
        time.sleep(0.5)
main()