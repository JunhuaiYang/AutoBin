from gpiozero import PWMLED
from time import sleep

led = PWMLED(23)

while True:
    led.value = 0  # 灭
    sleep(1)
    led.value = 0.5  # 半亮
    sleep(1)
    led.value = 1  # 全亮
    sleep(1)
    led.value = 0.5  # 半亮
    sleep(1)