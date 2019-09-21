from __future__ import print_function
import logging

import grpc

import protos.waste_pb2 as waste_pb2
import protos.waste_pb2_grpc as waste_pb2_grpc


def run():
    # NOTE(gRPC Python Team): .close() is possible on a channel and should be
    # used in circumstances in which the with statement does not fit the needs
    # of the code.
    with grpc.insecure_channel('localhost:50051') as channel:

        stub = waste_pb2_grpc.WasteServiceStub(channel)

        response = stub.WasteDetect(waste_pb2.WasteRequest(bin_id='11', waste_id='22', waste_image=b'123'))


    print("Greeter client received: %d" % response.res_id)


if __name__ == '__main__':
    logging.basicConfig()
    run()
