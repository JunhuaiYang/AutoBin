import time
import protos.waste_pb2 as waste_pb2
import protos.waste_pb2_grpc as waste_pb2_grpc
from gpiozero import RGBLED
import grpc
import logging
import cv2
from loggingconfig import log

SERVER_ADDR = '192.168.1.102:50051'
IMAGE_READ_TIME = 1

STATUS_LED:RGBLED = None
CAMERA = None
BKG_FRAME = None
CHANNEL:grpc.Channel = None
STUB:waste_pb2_grpc.WasteServiceStub = None

# 初始化状态
def Init():
    global STATUS_LED, CAMERA, BKG_FRAME, CHANNEL, STUB

    STATUS_LED = RGBLED(red=16, green=20, blue=21)
    STATUS_LED.blink(0.1, 0.1, on_color=(1, 0.6, 0)) # 闪灯
    # 首先连接相机
    CAMERA = cv2.VideoCapture(0)
    if CAMERA is None:
        raise SystemExit('摄像头连接失败')
    # 设置摄像头改变读取大小
    # CAMERA.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    # CAMERA.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
    # CAMERA.set(cv2.CAP_PROP_FRAME_WIDTH, 160)
    # CAMERA.set(cv2.CAP_PROP_FRAME_HEIGHT, 120)
    CAMERA.set(cv2.CAP_PROP_FOURCC, cv2.VideoWriter_fourcc('M', 'J', 'P', 'G'))

    pre_frame = None
    # 获得稳定的背景
    while True:
        res, cur_frame = CAMERA.read()
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
                BKG_FRAME = gray_img
                log.info('背景已保存')
                break

    # 连接远程服务器测试
    STATUS_LED.blink(0.1, 0.1, on_color=(0, 0.6, 0.6)) # 闪灯
    # 初始化连接状态
    CHANNEL = grpc.insecure_channel(SERVER_ADDR)
    STUB = waste_pb2_grpc.WasteServiceStub(CHANNEL)

    time.sleep(2)

    # 初始化完成
    STATUS_LED.color = (0, 1, 0)
    log.info('初始化完成')


def main():
    Init()
    #初始化 成功  进入运行状态
    while True:
        # response = STUB.WasteDetect(waste_pb2.WasteRequest(bin_id='11', waste_id='22', waste_image=b'123'))
        # print("Greeter client received: %d" % response.res_id)
        print('ischanged', isChange())

        time.sleep(IMAGE_READ_TIME)

def isChange(): 
    global BKG_FRAME
    res, cur_frame = CAMERA.read()
    gray_img = cv2.cvtColor(cur_frame, cv2.COLOR_BGR2GRAY)
    gray_img = cv2.resize(gray_img, (320, 240))
    gray_img = cv2.GaussianBlur(gray_img, (21, 21), 0) # 高斯滤波
    img_delta = cv2.absdiff(BKG_FRAME, gray_img)  # 取delta
    thresh = cv2.threshold(img_delta, 25, 255, cv2.THRESH_BINARY)[1]  # 图像阈值处理 转化为 0 1
    thresh = cv2.dilate(thresh, None, iterations=2)  # 膨胀操作
    image, contours, hierarchy = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)  # 查找轮廓
    for c in contours:
        if cv2.contourArea(c) < 1000: # 设置敏感度
            continue
        else:
            return True
    return False

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
    
    
