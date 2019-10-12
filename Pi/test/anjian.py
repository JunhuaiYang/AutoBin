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
        print(char)
except:
    curses.nocbreak()
    screen.keypad(0)
    curses.echo()
    
    #根据得到的值进行操作
    #无值为-1  其他为keyCode
 
