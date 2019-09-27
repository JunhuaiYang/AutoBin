import asyncio
import time
import logging

LOG_FORMAT = r"%(asctime)s %(levelname)s %(filename)s %(message)s "#配置输出日志格式
DATE_FORMAT = r'%Y-%m-%d %H:%M:%S ' #配置输出时间的格式，注意月份和天数不要搞乱了
logging.basicConfig(level=logging.DEBUG,
                    format=LOG_FORMAT,
                    )

async def asyncMoveMotor(num, direc):
    times = {1:1.5, 2:1.5, 3:1.5, 4:1.5}
    # self.motors[num].stop()
    logging.info('num: %d begin' % num)
    # if direc:
    #     self.motors[num].forward()
    # else:
    #     self.motors[num].backward()
    await asyncio.sleep(times[num])
    # self.motors[num].stop()
    logging.info('num: %d end' % num)


# time.strftime("%H_%M_%S", time.localtime()

tasks = []
tasks.append(asyncMoveMotor(1, 0))
tasks.append(asyncMoveMotor(2, 1))
tasks.append(asyncMoveMotor(3, 1))
tasks.append(asyncMoveMotor(4, 0))

loop = asyncio.get_event_loop()
loop.run_until_complete(asyncio.wait(tasks))