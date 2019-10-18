package handlers
import (
	pb "../protos"
	"context"
	"fmt"
	"google.golang.org/grpc"
	"log"
	"testing"
	"time"
)

// 垃圾检测测试
func Test_WasteDetect(test *testing.T) {
	var cc = grpc.ClientConn{}
	client := pb.NewWasteServiceClient(&cc)
	request := &pb.WasteRequest{
		BinId:"1",
		WasteImage:"sadfghgjh",
	}
	timeout := 3*time.Second
	ctx, _ := context.WithTimeout(context.Background(),timeout)
	reply, err := client.WasteDetect(ctx, request)
	if err != nil {
		log.Println(err.Error())
	}
	fmt.Println("检测结果类型为：",reply.ResId)
}

// 垃圾桶注册测试
func Test_BinRegister(test *testing.T) {
	var cc = grpc.ClientConn{}
	client := pb.NewWasteServiceClient(&cc)
	request := &pb.BinRegisterRequest{
		UserId:1,
	}
	timeout := 3*time.Second
	ctx, _ := context.WithTimeout(context.Background(),timeout)
	reply, err := client.BinRegister(ctx, request)
	if err != nil {
		log.Println(err.Error())
	}
	fmt.Println("注册获得垃圾桶Id为：",reply.BinId)
}

// 垃圾桶注册测试
func Test_BinStatus(test *testing.T) {
	var cc = grpc.ClientConn{}
	client := pb.NewWasteServiceClient(&cc)
	request := &pb.BinStatusRequest{
		BinId:1,
		Status:1,
	}
	timeout := 3*time.Second
	ctx, _ := context.WithTimeout(context.Background(),timeout)
	_, err := client.BinStatus(ctx, request)
	if err != nil {
		log.Println(err.Error())
		return
	}
	fmt.Println("垃圾桶状态上报成功")
}