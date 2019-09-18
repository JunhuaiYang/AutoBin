import logging
from logging import handlers
import datetime

today = datetime.date.today()
LOG_FORMAT = r"%(asctime)s %(levelname)s %(filename)s %(message)s "#配置输出日志格式
LOG_P_FORMAT = r"%(asctime)s %(levelname)s  %(message)s "#配置输出日志格式

DATE_FORMAT = r'%Y-%m-%d %H:%M:%S ' #配置输出时间的格式，注意月份和天数不要搞乱了
# FILE_NAME = './log/logs.txt'
FILE_NAME = r'.\log\log_{}{:02d}{:02d}.log'.format(today.year, today.month, today.day)

# logging.basicConfig(level=logging.DEBUG,
#                     format=LOG_FORMAT,
#                     datefmt = DATE_FORMAT ,
#                     filename= FILE_NAME #有了filename参数就不会直接输出显示到控制台，而是直接写入文件
#                     )
class Logger(object):
    level_relations = {
        'debug':logging.DEBUG,
        'info':logging.INFO,
        'warning':logging.WARNING,
        'error':logging.ERROR,
        'crit':logging.CRITICAL
    }#日志级别关系映射

    def __init__(self ,filename = FILE_NAME  ,level='debug' ,when='D',backCount=3):
        self.logger = logging.getLogger(filename)

        self.logger.setLevel(self.level_relations.get(level))#设置日志级别
        format_str = logging.Formatter(LOG_FORMAT)#设置日志格式
        format_p_str = logging.Formatter(LOG_P_FORMAT)#设置日志格式

        
        sh = logging.StreamHandler()#往屏幕上输出
        sh.setFormatter(format_p_str) #设置屏幕上显示的格式
        
        th = handlers.TimedRotatingFileHandler(filename=filename,when=when,backupCount=backCount,encoding='utf-8')#往文件里写入#指定间隔时间自动生成文件的处理器
        th.setFormatter(format_str)#设置文件里写入的格式
        
        self.logger.addHandler(sh) #把对象加到logger里
        self.logger.addHandler(th)
    
# 实例
log = Logger().logger
