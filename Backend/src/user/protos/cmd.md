
### 生成*.pb.go
    protoc -I%GOROOT% -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --go_out=plugins=grpc:. ./user.proto
### 生成*.pb.gw.go
    protoc -I%GOROOT% -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --grpc-gateway_out=logtostderr=true:. ./user.proto 
### 生成swagger.json
    protoc -I%GOROOT% -I. -I%GOPATH%/src -I%GOPATH%/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis --swagger_out=logtostderr=true:. ./user.proto