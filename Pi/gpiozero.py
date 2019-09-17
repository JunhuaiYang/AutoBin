from gpiozero import LED
from time import sleep

red = LED(23)  #led的正极接GPIO17

while True:
    red.on()   #开灯
    sleep(1)
    red.off()  #关灯
    sleep(1)