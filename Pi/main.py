import time
import protos.waste_pb2 as waste_pb2
import protos.waste_pb2_grpc as waste_pb2_grpc
import gpiozero
import grpc
import logging
from loggingconfig import log

def main():
    # with grpc.insecure_channel('localhost:50051') as channel:

    #     stub = waste_pb2_grpc.WasteServiceStub(channel)

    #     response = stub.WasteDetect(waste_pb2.WasteRequest(bin_id='11', waste_id='22', waste_image=b'123'))

    #     while True:
    #         response = stub.WasteDetect(waste_pb2.WasteRequest(bin_id='11', waste_id='22', waste_image=b'123'))
    #         print("Greeter client received: %d" % response.res_id)
    #         time.sleep(1000)
    while True:
        print('go')
        time.sleep(2)

if __name__ == '__main__':
    log.info('server start')
    try:
        main()
    except KeyboardInterrupt:
        log.info('server stop with KeyboardInterrupt')
    except Exception as ex:
        log.warning('server stop with {}'.format(ex))
    
    