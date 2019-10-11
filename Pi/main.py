import time
import datetime
import protos.waste_pb2 as waste_pb2
import protos.waste_pb2_grpc as waste_pb2_grpc
from gpiozero import RGBLED
import grpc
import logging
import cv2
from loggingconfig import log
from picamera import PiCamera
from io import BytesIO
import numpy as np
import base64
from MotorCtrl import motor
import socket



SERVER_ADDR = '192.168.1.107:50051'
# SERVER_ADDR = '192.168.1.199:8181'
IMAGE_READ_TIME = 1
GARBAGE = {-1:'未识别', 0:'干垃圾', 1:'湿垃圾', 2:'有害垃圾', 3:'可回收垃圾'}
# 控制对象全局变量
STATUS_LED:RGBLED = None
CAMERA = None
BKG_FRAME = None
CHANNEL:grpc.Channel = None
STUB:waste_pb2_grpc.WasteServiceStub = None
# 用户全局变量
BIN_ID = -1
USER_ID = 1
MY_ADDR = None

def main():
    Init()
    #初始化 成功  进入运行状态
    while True:

        flag, image = isChange()
        if flag:
            log.info('有垃圾进入')
            STATUS_LED.blink(0.1, 0.1, on_color=(0.7, 0.1, 0.6)) # 闪灯 处理垃圾中
            image64 = base64.b64encode(image)
            response = STUB.WasteDetect(waste_pb2.WasteRequest(bin_id=str(BIN_ID), waste_image=image64))
            response_id = response.res_id
            if response_id>-1:
                log.info('垃圾识别结果：{}'.format(GARBAGE[response_id]))
                motor.Garbge(response_id)
                # 重新更新背景
                BKG_FRAME = GetBackGround()

                # 处理完成
                STATUS_LED.color = (0, 1, 0)
            else:
                log.info('垃圾识别失败！')
                STATUS_LED.color = (1, 0, 0)
            time.sleep(1)

        time.sleep(IMAGE_READ_TIME)


# 初始化状态
def Init():
    global STATUS_LED, CAMERA, BKG_FRAME, CHANNEL, STUB, MY_ADDR, USER_ID

    STATUS_LED = RGBLED(red=16, green=20, blue=21)
    STATUS_LED.blink(0.1, 0.1, on_color=(1, 0.6, 0)) # 闪灯
    # 首先连接相机
    CAMERA = PiCamera()
    CAMERA.resolution = (640, 480)
    if CAMERA is None:
        raise SystemExit('摄像头连接失败')
    # 摄像头预热
    time.sleep(5)
    
    # 获得一个稳定的背景
    BKG_FRAME = GetBackGround()

    # 连接远程服务器测试
    STATUS_LED.blink(0.1, 0.1, on_color=(0, 0.6, 0.6)) # 闪灯
    # 初始化连接状态
    CHANNEL = grpc.insecure_channel(SERVER_ADDR)
    STUB = waste_pb2_grpc.WasteServiceStub(CHANNEL)
    MY_ADDR = get_host_ip()
    log.info('当前IP地址为:{}'.format(MY_ADDR))
    BIN_ID = STUB.BinRegister(waste_pb2.BinRegisterRequest(user_id = USER_ID, ip_address = MY_ADDR)).bin_id
    log.info('向服务器注册成功！当前垃圾桶ID为{}'.format(BIN_ID))

    time.sleep(1)

    # 初始化完成
    STATUS_LED.color = (0, 1, 0)
    log.info('初始化完成')

def GetBackGround():
    my_stream = BytesIO()
    pre_frame = None
    # 获得稳定的背景
    while True:
        time.sleep(1)
        CAMERA.capture(my_stream, 'jpeg')
        image_data = my_stream.getvalue()
        nparr = np.frombuffer(image_data, np.uint8)
        cur_frame = cv2.imdecode(nparr, 1)

        gray_img = cv2.cvtColor(cur_frame, cv2.COLOR_BGR2GRAY)
        gray_img = cv2.resize(gray_img, (320 , 240))
        gray_img = cv2.GaussianBlur(gray_img, (21, 21), 0) # 高斯滤波
        if pre_frame is None:
            pre_frame = gray_img
        else:
            img_delta = cv2.absdiff(pre_frame, gray_img)  # 取delta
            thresh = cv2.threshold(img_delta, 25, 255, cv2.THRESH_BINARY)[1]  # 图像阈值处理 转化为 0 1
            thresh = cv2.dilate(thresh, None, iterations=2)  # 膨胀操作
            image, contours, hierarchy = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)  # 查找轮廓
            if len(contours) == 0:
                bkg = gray_img
                log.info('背景已保存')
                break
    return bkg


def isChange(): 
    global BKG_FRAME

    my_stream = BytesIO()
    CAMERA.capture(my_stream, 'jpeg')
    image_data = my_stream.getvalue()
    nparr = np.frombuffer(image_data, np.uint8)
    cur_frame = cv2.imdecode(nparr, 1)
    
    gray_img = cv2.cvtColor(cur_frame, cv2.COLOR_BGR2GRAY)
    gray_img = cv2.resize(gray_img, (320, 240))
    gray_img = cv2.GaussianBlur(gray_img, (21, 21), 0) # 高斯滤波 高斯模糊
    img_delta = cv2.absdiff(BKG_FRAME, gray_img)  # 取delta
    thresh = cv2.threshold(img_delta, 25, 255, cv2.THRESH_BINARY)[1]  # 图像阈值处理 转化为 0 1
    thresh = cv2.dilate(thresh, None, iterations=2)  # 膨胀操作
    image, contours, hierarchy = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)  # 查找轮廓
    for c in contours:
        if cv2.contourArea(c) < 1000: # 设置敏感度
            continue
        else:
            return True, image_data
    return False, None


# 通过建立一个UDP连接来获得IP地址
def get_host_ip():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(('8.8.8.8', 80))
        ip = s.getsockname()[0]
    finally:
        s.close()
    return ip


if __name__ == '__main__':
    log.info('server start')
    try:
        main()
    except KeyboardInterrupt:
        log.info('server stop with KeyboardInterrupt')
    except SystemExit as ex:
        log.error('server stop with {}'.format(ex))
    except Exception as ex:
        log.exception(ex)
    finally:
        CHANNEL.close()
        CAMERA.close()
    
    
