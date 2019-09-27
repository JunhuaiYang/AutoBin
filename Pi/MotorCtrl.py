from gpiozero import Motor
from time import sleep
import asyncio

class MotorCtrl:
    def __init__(self):
        self.motor1 = Motor(26, 19)
        self.motor2 = Motor(5, 6) 
        self.motor3 = Motor(12, 13)
        self.motor4 = Motor(23, 24)
        self.motors = (None, self.motor1, self.motor2, self.motor3, self.motor4)
        self.loop = asyncio.get_event_loop()

    def MoveMotor(self, num, direc, time):
        self.motors[num].stop()
        if direc:
            self.motors[num].forward()
        else:
            self.motors[num].backward()
        sleep(time)
        self.motors[num].stop()

    async def asyncMoveMotor(self, num, direc):
        times = {1:1.5, 2:1.55, 3:1.58, 4:1.52}
        self.motors[num].stop()
        if direc:
            self.motors[num].forward()
        else:
            self.motors[num].backward()
        await asyncio.sleep(times[num])
        self.motors[num].stop()
    
    # 垃圾类别id，-1：未识别； 0：干垃圾； 1： 湿垃圾； 2：有害； 3：可回收'
    def MovePan(self, types):
        tasks = []
        if types == 0:
            tasks.append(self.asyncMoveMotor(1, 1))
            tasks.append(self.asyncMoveMotor(2, 1))
            tasks.append(self.asyncMoveMotor(3, 0))
            tasks.append(self.asyncMoveMotor(4, 0))
        elif types == 1:
            tasks.append(self.asyncMoveMotor(1, 0))
            tasks.append(self.asyncMoveMotor(2, 1))
            tasks.append(self.asyncMoveMotor(3, 1))
            tasks.append(self.asyncMoveMotor(4, 0))
        elif types == 2:
            tasks.append(self.asyncMoveMotor(1, 0))
            tasks.append(self.asyncMoveMotor(2, 0))
            tasks.append(self.asyncMoveMotor(3, 1))
            tasks.append(self.asyncMoveMotor(4, 1))
        elif types == 3:
            tasks.append(self.asyncMoveMotor(1, 1))
            tasks.append(self.asyncMoveMotor(2, 0))
            tasks.append(self.asyncMoveMotor(3, 0))
            tasks.append(self.asyncMoveMotor(4, 1))
        else:
            return

        self.loop.run_until_complete(asyncio.wait(tasks))

    def MovePanFlat(self, types):
        tasks = []
        if types == 0:
            tasks.append(self.asyncMoveMotor(1, 0))
            tasks.append(self.asyncMoveMotor(2, 0))
            tasks.append(self.asyncMoveMotor(3, 1))
            tasks.append(self.asyncMoveMotor(4, 1))
        elif types == 1:
            tasks.append(self.asyncMoveMotor(1, 1))
            tasks.append(self.asyncMoveMotor(2, 0))
            tasks.append(self.asyncMoveMotor(3, 0))
            tasks.append(self.asyncMoveMotor(4, 1))
        elif types == 2:
            tasks.append(self.asyncMoveMotor(1, 1))
            tasks.append(self.asyncMoveMotor(2, 1))
            tasks.append(self.asyncMoveMotor(3, 0))
            tasks.append(self.asyncMoveMotor(4, 0))
        elif types == 3:
            tasks.append(self.asyncMoveMotor(1, 0))
            tasks.append(self.asyncMoveMotor(2, 1))
            tasks.append(self.asyncMoveMotor(3, 1))
            tasks.append(self.asyncMoveMotor(4, 0))
        else:
            return

        self.loop.run_until_complete(asyncio.wait(tasks))

    def allMove(self, dirc):
        tasks = []
        tasks.append(self.asyncMoveMotor(1, dirc))
        tasks.append(self.asyncMoveMotor(2, dirc))
        tasks.append(self.asyncMoveMotor(3, dirc))
        tasks.append(self.asyncMoveMotor(4, dirc))
        self.loop.run_until_complete(asyncio.wait(tasks))
    
    def Garbge(self, types):
        self.MovePan(types)
        sleep(1)
        self.MovePanFlat(types)



motor = MotorCtrl()

if __name__ == "__main__":
    while True:
        # n, d, t = map(int, input('编号 方向 时间: ').split())
        # motor.MoveMotor(n, d, t)
        d = int(input())
        motor.allMove(d)