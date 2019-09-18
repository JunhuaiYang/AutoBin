# 环境安装

```
python -m pip install grpcio
python -m pip install grpcio-tools
python -m pip install googleapis-common-protos
```

# 编译方法
```
python -m grpc_tools.protoc -I./ --python_out=. --grpc_python_out=. ./hello.proto
```
## 编译时需要获得Google Api
https://github.com/googleapis/googleapis