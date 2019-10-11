import socket
 
#多网卡情况下，根据前缀获取IP
def GetLocalIPByPrefix(prefix):
    localIP = ''
    for ip in socket.gethostbyname_ex(socket.gethostname())[2]:
        if ip.startswith(prefix):
            localIP = ip
     
    return localIP
     
     
print(GetLocalIPByPrefix('192.168'))