from picamera import PiCamera
import time

camera = PiCamera()
camera.resolution = (640, 480)

while True:
    time1 = time.time()
    camera.capture('img/{}.jpg'.format(time.strftime("%H:%M:%S", time.localtime())))
    time2 = time.time()
    print(time2 - time1)
    # cv2.imwrite('{}.jpg'.format(time.strftime("%H-%M-%S", time.localtime()) ),cur_frame, [int( cv2.IMWRITE_JPEG_QUALITY), 80])
    time.sleep(1)