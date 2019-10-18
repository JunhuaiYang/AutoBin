package main

import (
	pb "../protos"
	"context"
	"google.golang.org/grpc"
	"log"
	"sync"
	"time"
)

const (
	address     = "127.0.0.1:8181"
)

func test_WasteDetect (c pb.WasteServiceClient) {
	//5秒超时
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)
	defer cancel()
	request := &pb.WasteRequest{
		BinId:"1",
		WasteImage:"sadfghgjh",
	}
	r, err := c.WasteDetect(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Printf("ResId: %s", r.ResId)
}

func test_BinStatus (c pb.WasteServiceClient) {
	//5秒超时
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)
	defer cancel()
	request := &pb.BinStatusRequest{
		BinId:1,
		Status:1,
	}
	_, err := c.BinStatus(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}

}

func test_BinRegister (c pb.WasteServiceClient) {
	//5秒超时
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)
	defer cancel()
	request := &pb.BinRegisterRequest{
		UserId:1,
	}
	r, err := c.BinRegister(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Printf("BinId: %s", r.BinId)
}

func main() {
	// grpc.Dial负责和GRPC服务建立链接
	conn, err := grpc.Dial(address, grpc.WithInsecure())
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}
	defer conn.Close()
	// 然后NewGreeterClient函数基于已经建立的链接构造GreeterClient对象
	// 返回的client其实是一个NewWasteServiceClient接口对象，通过接口定义的方法就可以调用服务端对应的GRPC服务提供的方法。
	c := pb.NewWasteServiceClient(conn)
	wg := sync.WaitGroup{}
	wg.Add(3)
	go func() {
		test_WasteDetect(c)
		wg.Done()
	}()
	go func() {
		//test_BinStatus(c)
		wg.Done()
	}()
	go func() {
		//test_BinRegister(c)
		wg.Done()
	}()
	wg.Wait()
}

