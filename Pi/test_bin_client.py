from __future__ import print_function
import logging
import grpc

import protos.BinServer_pb2 as BinServer_pb2
import protos.BinServer_pb2_grpc as BinServer_pb2_grpc

def run():
    with grpc.insecure_channel('192.168.1.110:8081') as channel:
    # with grpc.insecure_channel('192.168.1.199:8181') as channel:
        stub = BinServer_pb2_grpc.BinServiceStub(channel)
        try:
            response = stub.BinStatus(BinServer_pb2.StatusRequest(user_id = 1))
            # response = stub.BinMotor(BinServer_pb2.MotorRequest(user_id = 1, motor = 1, dirc = 1))

            print(response)
        except Exception as ex:
            print(ex)
            if 'connect' in ex.__str__():
                print('in')
        


    # print("Greeter client received: %d" % response.res_id)
    # print("Greeter client received: %d" % response.bin_id)

if __name__ == '__main__':
    logging.basicConfig()
    run()
