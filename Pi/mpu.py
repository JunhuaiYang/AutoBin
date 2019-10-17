from mpu6050 import mpu6050
from time import sleep
import math

offsetX = 0
offsetY = 0

with open('mpu_offset.txt', 'r') as ff:
    offsetX, offsetY = map(float, ff.read().split())

def GetAngel():
    mpu = mpu6050(0x68)
    accel_data = mpu.get_accel_data()
    xdata = accel_data['x'] + offsetX
    ydata = accel_data['y'] + offsetY
    zdata = accel_data['z']
    angel = math.atan(math.sqrt(xdata**2 + ydata**2 ) / zdata )
    return math.degrees(abs(angel))

def GetTemp():
    mpu = mpu6050(0x68)
    return mpu.get_temp()

def GetOffset():
    mpu = mpu6050(0x68)
    xx = []
    yy = []
    zz = []
    print('正在校准数据')
    for i in range(100):
        accel_data = mpu.get_accel_data()
        print(i,accel_data)
        xx.append(accel_data['x'] )
        yy.append(accel_data['y'])
        zz.append(accel_data['z'] )
        sleep(0.05)

    x = sum(xx) / len(xx)
    y = sum(yy) / len(yy)
    z = sum(zz) / len(zz)
    with open('mpu_offset.txt', 'w') as f:
        f.write('{:.3f} {:.3f}'.format(-x, -y))
    print(x, y, z)
    print('校准成功')

if __name__ == "__main__":
    GetOffset()