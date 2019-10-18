
### 生成*.pb.go
### 生成*.pb.gw.go
### 生成swagger.json

    protoc -I%GOROOT% -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --go_out=plugins=grpc:. ./waste.proto
    protoc -I%GOROOT% -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --grpc-gateway_out=logtostderr=true:. ./waste.proto 
    protoc -I%GOROOT% -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --swagger_out=logtostderr=true:. ./waste.proto
                