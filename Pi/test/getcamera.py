import cv2
camera = cv2.VideoCapture(0)
if camera is None:
    print('请先连接摄像头')
    exit()
 
camera.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
camera.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

print('CAP_PROP_FRAME_WIDTH' ,camera.get(cv2.CAP_PROP_FRAME_WIDTH))
print('CAP_PROP_FRAME_HEIGHT' ,camera.get(cv2.CAP_PROP_FRAME_HEIGHT))
print('CAP_PROP_FPS' ,camera.get(cv2.CAP_PROP_FPS))
print('CAP_PROP_BRIGHTNESS' ,camera.get(cv2.CAP_PROP_BRIGHTNESS))
