from mpu6050 import mpu6050
from time import sleep
mpu = mpu6050(0x68)
import math
# accelerometer_data = sensor.get_accel_data()
# accel_data = mpu.get_accel_data()
# # print(accel_data['x'])
# # print(accel_data['y'])
# # print(accel_data['z'])
# print(accel_data)
# gyro_data = mpu.get_gyro_data()
# print(gyro_data)

# # print(gyro_data['x'])
# # print(gyro_data['y'])
# # print(gyro_data['z'])

# print(mpu.get_temp())

while(True):
    accel_data = mpu.get_accel_data()
    # gyro_data = mpu.get_gyro_data()
    print('a',accel_data)
    angel = math.atan(math.sqrt(accel_data['x']**2 + accel_data['y']**2 ) / accel_data['z'] )
    print(math.degrees(angel))
    # print('g',gyro_data)
    sleep(1)
    
xx = []
yy = []
zz = []
for i in range(200):
    accel_data = mpu.get_accel_data()
    print(i,accel_data)
    xx.append(accel_data['x'] )
    yy.append(accel_data['y'])
    zz.append(accel_data['z'] )
    sleep(0.5)

x = sum(xx) / len(xx)
y = sum(yy) / len(yy)
z = sum(zz) / len(zz)

print(x, y, z)