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
    
    # 垃圾类别id，-1:'未识别', 0:'可回收垃圾', 1:'有害垃圾', 2:'湿垃圾', 3:'干垃圾'
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

    # 平板复原
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

    # 所有同时移动
    def allMove(self, dirc):
        tasks = []
        tasks.append(self.asyncMoveMotor(1, dirc))
        tasks.append(self.asyncMoveMotor(2, dirc))
        tasks.append(self.asyncMoveMotor(3, dirc))
        tasks.append(self.asyncMoveMotor(4, dirc))
        self.loop.run_until_complete(asyncio.wait(tasks))
    
    # 具体垃圾信息
    def Garbge(self, types):
        self.MovePan(types)
        sleep(1)
        self.MovePanFlat(types)
        # 板子平衡问题  需要加一些补偿
        if types == 0:
            self.MoveMotor(1, 1, 0.02)
            self.MoveMotor(2, 1, 0.05)
            self.MoveMotor(3, 1, 0.06)
            self.MoveMotor(4, 1, 0.03)
        elif types == 1:
            self.MoveMotor(1, 1, 0.02)
            self.MoveMotor(2, 1, 0.05)
            self.MoveMotor(3, 1, 0.07)
            self.MoveMotor(4, 1, 0.04)
        elif types == 2:
            self.MoveMotor(1, 1, 0.02)
            self.MoveMotor(2, 1, 0.03)
            self.MoveMotor(3, 1, 0.05)
            self.MoveMotor(4, 1, 0.05)
        elif types == 3:
            self.MoveMotor(1, 1, 0.02)
            self.MoveMotor(2, 1, 0.02)
            self.MoveMotor(3, 1, 0.04)
            self.MoveMotor(4, 1, 0.05)



motor = MotorCtrl()

if __name__ == "__main__":
    import curses
    #初始化curses
    screen=curses.initscr()
    #设置不回显
    curses.noecho()
    #设置不需要按回车立即响应
    curses.cbreak()
    #开启键盘模式
    screen.keypad(1)
    #阻塞模式读取0 非阻塞 1
    screen.nodelay(0)  

    try:
        while(True):
            char=screen.getch()
            if char == 49:  # 1
                motor.MoveMotor(1, 1, 0.1)
            elif char == 50: # 2
                motor.MoveMotor(2, 1, 0.1)
            elif char == 51: # 3
                motor.MoveMotor(3, 1, 0.1)
            elif char == 52: # 4
                motor.MoveMotor(4, 1, 0.1)
            elif char == 113:  # Q
                motor.MoveMotor(1, 0, 0.1)
            elif char == 119: # 2
                motor.MoveMotor(2, 0, 0.1)
            elif char == 101: # 3
                motor.MoveMotor(3, 0, 0.1)
            elif char == 114: # 4
                motor.MoveMotor(4, 0, 0.1)
            else:
                pass
    except:
        curses.nocbreak()
        screen.keypad(0)
        curses.echo()

    # while True:
    #     # n, d, t = map(int, input('编号 方向 时间: ').split())
    #     # motor.MoveMotor(n, d, t)
    #     d = int(input())
    #     motor.allMove(d)