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
import json
import os
import mpu
import protos.BinServer_pb2 as BinServer_pb2
import protos.BinServer_pb2_grpc as BinServer_pb2_grpc
from concurrent import futures


SERVER_ADDR = '192.168.1.107:50051'
# SERVER_ADDR = '192.168.1.199:8181'
IMAGE_READ_TIME = 1
RECONN_TIME = 10
STATUS_TIME = 20
# GARBAGE = {-1:'未识别', 0:'干垃圾', 1:'湿垃圾', 2:'有害垃圾', 3:'可回收垃圾'}
GARBAGE = {-1:'未识别', 0:'可回收垃圾', 1:'有害垃圾', 2:'湿垃圾', 3:'干垃圾'}
# 控制对象全局变量
STATUS_LED:RGBLED = None
CAMERA = None
BKG_FRAME = None
CHANNEL:grpc.Channel = None
STUB:waste_pb2_grpc.WasteServiceStub = None
CONNECTION_FLAG = False
MPU = None
ACCEL_DATA = None  # 陀螺仪数据
SERVER = None   # 本地服务器
# 用户全局变量
BIN_ID = -1
USER_ID = -1
MY_ADDR = None
ID_DIRC = {'BIN_ID':-1, 'USER_ID':1}
REGIS_FLAG = False
STATUS = 0
# 状态定义 -2:垃圾识别失败，需要取出垃圾 -1：连接失败  1：正常  2：正在处理垃圾  3：平板角度有问题, 需要处理  


def main():
    global STATUS_LED, CAMERA, BKG_FRAME, CHANNEL, STUB, MY_ADDR, USER_ID, CONNECTION_FLAG, RECONN_TIME, STATUS, STATUS_TIME
    count = 0

    Init()
    wait_flag = False

    #初始化 成功  进入运行状态
    while True:
        if not CONNECTION_FLAG:
            STATUS = -1
            log.info('正在尝试重连')
            if not Register():
                time.sleep(RECONN_TIME)
                continue

        flag, image = isChange()
        if flag:
            if not wait_flag:
                STATUS = 2
                STATUS_LED.blink(0.1, 0.1, on_color=(0.7, 0.1, 0.6)) # 闪灯 处理垃圾中
                log.info('有垃圾进入') # 延时一段时间
                time.sleep(1)
                wait_flag = True
                continue
            else:
                wait_flag = False

            image64 = base64.b64encode(image)
            try:
                log.info('正在识别')
                response = STUB.WasteDetect(waste_pb2.WasteRequest(bin_id=str(BIN_ID), waste_image=image64))
            except Exception as ex:
                if 'connect' in ex.__str__(): # 连接失败
                    CONNECTION_FLAG = False
                    log.info('服务器连接失败')
                    STATUS_LED.color = (1, 0.4, 0.0) # 连接失败
                else:
                    log.error('未知错误')
                    log.error(ex)

            response_id = response.res_id
            response_name = response.waste_name
            if response_id>-1:
                log.info('垃圾识别结果：{}  属于：{}'.format(response_name, GARBAGE[response_id]))
                motor.Garbge(response_id)
                time.sleep(2) # 等待稳定
                # 重新更新背景
                BKG_FRAME = GetBackGround()
                # 处理完成
                STATUS_LED.color = (0, 1, 0)
                STATUS = 1
            else:
                log.info('垃圾识别失败！ 识别结果：{}'.format(response_name))
                STATUS_LED.color = (1, 0, 0)
                time.sleep(5)
                STATUS = -2
            count = STATUS_TIME+1
        else:
            STATUS_LED.color = (0, 1, 0)  # 进入运行状态
            wait_flag = False


        # 判断MPU
        mpu_angel = mpu.GetAngel()
        mpu_temp = mpu.GetTemp()
        if mpu_angel > 5:  # 当前偏转角度过大  需要处理一下
            STATUS_LED.color = (0.8, 0, 0.4)
            STATUS = 3

        if count > STATUS_TIME:
            count = 0
            try:
                STUB.BinStatus(waste_pb2.BinStatusRequest(bin_id=BIN_ID, status=STATUS, angel=mpu_angel, temp = mpu_temp))
            except Exception as ex:
                if 'connect' in ex.__str__(): # 连接失败
                    CONNECTION_FLAG = False
                    log.info('服务器连接失败')
                    STATUS_LED.color = (1, 0.4, 0.0) # 连接失败
                else:
                    log.error('未知错误')
                    log.error(ex)
            log.info('向服务器上报状态： 当前状态：{}  当前平板角度：{:.2F}°  当前温度:{:.1F}°C'.format(STATUS, mpu_angel, mpu_temp))
        else:
            count+=1


        time.sleep(IMAGE_READ_TIME)


# 初始化状态
def Init():
    global STATUS_LED, CAMERA, BKG_FRAME, CHANNEL, STUB, MY_ADDR, USER_ID, BIN_ID, CONNECTION_FLAG, ID_DIRC, REGIS_FLAG, MPU

    if os.path.exists('ID.json'):
        ID_DIRC = json.load(open('ID.json'))
        USER_ID = ID_DIRC['USER_ID']
        BIN_ID = ID_DIRC['BIN_ID']
        REGIS_FLAG = True
    else:
        json.dump(ID_DIRC, open('ID.json', 'w'))
    
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

    # 初始化连接状态
    CHANNEL = grpc.insecure_channel(SERVER_ADDR)
    STUB = waste_pb2_grpc.WasteServiceStub(CHANNEL)
    MY_ADDR = get_host_ip()
    log.info('当前IP地址为: {}'.format(MY_ADDR))
    # 启动反向服务
    StartSever()

    # 初始化完成
    log.info('初始化完成')
    # 最后注册垃圾桶
    Register()

def Register():
    global BIN_ID, USER_ID, MY_ADDR, CONNECTION_FLAG, STATUS_LED, REGIS_FLAG, ID_DIRC, STATUS
    # 连接远程服务器测试
    STATUS_LED.blink(0.1, 0.1, on_color=(0, 0.6, 0.6)) # 闪灯
    try:
        BIN_ID = STUB.BinRegister(waste_pb2.BinRegisterRequest(user_id = USER_ID, ip_address = MY_ADDR, bin_id = BIN_ID)).bin_id
        if not REGIS_FLAG:
            ID_DIRC['BIN_ID'] = BIN_ID
            json.dump(ID_DIRC, open('ID.json', 'w'))
    except Exception as ex:
        if 'connect' in ex.__str__():
            CONNECTION_FLAG = False
            log.info('服务器连接失败')
            STATUS_LED.color = (1, 0.4, 0) # 连接失败
            return CONNECTION_FLAG
        else:
            log.error('未知错误')
            log.error(ex)
            return False
    log.info('向服务器注册成功！当前垃圾桶ID为：{}'.format(BIN_ID))
    STATUS_LED.color = (0, 1, 0) # 正常状态
    STATUS = 1
    CONNECTION_FLAG = True
    return CONNECTION_FLAG

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
        # 图片剪裁
        # 裁剪坐标为[y0:y1, x0:x1]
        cur_frame = cur_frame[0:455, 10:500]

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
                log.info('背景已更新')
                break
            else:
                pre_frame = gray_img

    return bkg


def isChange(): 
    global BKG_FRAME

    my_stream = BytesIO()
    CAMERA.capture(my_stream, 'jpeg')
    image_data = my_stream.getvalue()
    nparr = np.frombuffer(image_data, np.uint8)
    cur_frame = cv2.imdecode(nparr, 1)
    # 图片剪裁
    cur_frame = cur_frame[0:455, 10:500]
    
    gray_img = cv2.cvtColor(cur_frame, cv2.COLOR_BGR2GRAY)
    gray_img = cv2.resize(gray_img, (320, 240))
    gray_img = cv2.GaussianBlur(gray_img, (21, 21), 0) # 高斯滤波 高斯模糊
    img_delta = cv2.absdiff(BKG_FRAME, gray_img)  # 取delta
    thresh = cv2.threshold(img_delta, 25, 255, cv2.THRESH_BINARY)[1]  # 图像阈值处理 转化为 0 1
    thresh = cv2.dilate(thresh, None, iterations=2)  # 膨胀操作
    image, contours, hierarchy = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)  # 查找轮廓

    # image_data = cv2.imencode('.jpg', cur_frame)[1]
    # time.sleep(1)
    # return True, image_data
    for c in contours:
        if cv2.contourArea(c) < 1000: # 设置敏感度
            continue
        else:
            image_data = cv2.imencode('.jpg', cur_frame)[1]
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


# 远程调用服务器创建
class BinServer(BinServer_pb2_grpc.BinServiceServicer):
    # 调用电机
    def BinMotor(self, request, context):
        global STATUS_LED, CAMERA, USER_ID, STATUS
        user = request.user_id
        num = request.motor
        dirc = request.dirc
        motor.MoveMotor(num, dirc, 0.1)
        log.info('来自用户{} 手动模式 电机{} 方向:{}  '.format(user, num, dirc))
        return BinServer_pb2.NULL()

    def BinStatus(self, request, context):
        global STATUS_LED, CAMERA, USER_ID, STATUS
        mpu_angel = mpu.GetAngel()
        mpu_temp = mpu.GetTemp()
        log.info('来自用户{} 状态获取  当前状态：{}  当前平板角度：{:.2F}°  当前温度:{:.1F}°C'.format(request.user_id, STATUS,mpu_angel ,mpu_temp ))
        return BinServer_pb2.StatusReply(status = STATUS, angel = mpu_angel, temp = mpu_temp)

    def BinImage(self, request, context):
        global STATUS_LED, CAMERA, USER_ID, STATUS
        my_stream = BytesIO()
        CAMERA.capture(my_stream, 'jpeg')
        log.info('来自用户{} 图片获取 '.format(request.user_id))
        return BinServer_pb2.ImageReply(image = my_stream.getvalue())

def StartSever():
    global SERVER
    SERVER = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    BinServer_pb2_grpc.add_BinServiceServicer_to_server(BinServer(), SERVER)
    SERVER.add_insecure_port('[::]:8081')
    SERVER.start()
    log.info('gRPC服务器启动成功 地址为{}:8081'.format(MY_ADDR))


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
    
    
