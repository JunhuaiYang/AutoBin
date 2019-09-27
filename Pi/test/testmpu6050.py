from mpu6050 import mpu6050
sensor = mpu6050(0x68)
print(accelerometer_data = sensor.get_accel_data())
# accelerometer_data = sensor.get_accel_data()