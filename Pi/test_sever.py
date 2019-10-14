from concurrent import futures
import logging

import grpc

import protos.waste_pb2 as waste_pb2
import protos.waste_pb2_grpc as waste_pb2_grpc

import time
import base64


class Greeter(waste_pb2_grpc.WasteServiceServicer):

    def WasteDetect(self, request, context):
        """传输实时图片 返回识别结果
        """
        print('垃圾桶id：{} --- {}.jpg '.format(request.bin_id, time.strftime("%H.%M.%S", time.localtime())))
        image = base64.b64decode(request.waste_image)
        with open('img\{}.jpg'.format(time.strftime("1-%H.%M.%S", time.localtime())),'wb') as f:
            f.write(image)
        return waste_pb2.WasteReply(res_id = 3)

    def BinRegister(self, request, context):
        print('{} bin_id:{}  IP:{}'.format(request.user_id, request.bin_id, request.ip_address))
        return waste_pb2.BinRegisterReply(bin_id = 1)

    # def SayHello(self, request, context):
    #     return waste_pb2.HelloReply(message='Hello, %s!' % request.name)


def serve():
    servers = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    waste_pb2_grpc.add_WasteServiceServicer_to_server(Greeter(), servers)
    servers.add_insecure_port('[::]:50051')
    servers.start()
    # servers.wait_for_termination()
    while True:
        time.sleep(1000)


if __name__ == '__main__':
    logging.basicConfig()
    serve()
