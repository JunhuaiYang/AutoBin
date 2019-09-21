import time
import protos.waste_pb2 as waste_pb2
import protos.waste_pb2_grpc as waste_pb2_grpc
from gpiozero import RGBLED
import grpc
import logging
import cv2
from loggingconfig import log

SERVER_ADDR = 

STATUS_LED:RGBLED = None
CAMERA = None
BKG_FRAME = None

# 初始化状态
def Init():
    STATUS_LED = RGBLED(red=16, green=20, blue=21)
    STATUS_LED.blink(0.1, 0.1, on_color=(1, 0.6, 0)) # 闪灯
    # 首先连接相机
    CAMERA = cv2.VideoCapture(0)
    if CAMERA is None:
        raise SystemExit('摄像头连接失败')
    
    # 设置摄像头长款
    CAMERA.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    CAMERA.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

    pre_frame = None
    res, cur_frame = CAMERA.read()
    # 获得稳定的背景
    while True:
        gray_img = cv2.cvtColor(cur_frame, cv2.COLOR_BGR2GRAY)
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
    time.sleep(5)

    # 初始化完成
    STATUS_LED.color = (0, 1, 0)
    log.info('初始化完成')


def main():
    with grpc.insecure_channel('localhost:50051') as channel:

        stub = waste_pb2_grpc.WasteServiceStub(channel)

        response = stub.WasteDetect(waste_pb2.WasteRequest(bin_id='11', waste_id='22', waste_image=b'123'))

        while True:
            response = stub.WasteDetect(waste_pb2.WasteRequest(bin_id='11', waste_id='22', waste_image=b'123'))
            print("Greeter client received: %d" % response.res_id)
            time.sleep(1000)

    Init()


    #初始化 成功  进入运行状态
    while True:
        print('go')
        time.sleep(2)



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
    
    
