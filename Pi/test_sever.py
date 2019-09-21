from concurrent import futures
import logging

import grpc

import protos.waste_pb2 as waste_pb2
import protos.waste_pb2_grpc as waste_pb2_grpc

import time


class Greeter(waste_pb2_grpc.WasteServiceServicer):

    def WasteDetect(self, request, context):
        """传输实时图片 返回识别结果
        """
        print('{} {} {}'.format(request.bin_id, request.waste_id, request.waste_image))
        return waste_pb2.WasteReply(res_id = 111)

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
