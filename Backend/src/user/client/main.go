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
// 用户登录
func test_UserLogin (c pb.UserServiceClient) {
	//5秒超时
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)
	defer cancel()
	request := &pb.LoginRequest{
		UserId:"1",
		UserPassword:"1",
	}
	_, err := c.UserLogin(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Printf("login success", )
}

// 用户注册
func test_Register (c pb.UserServiceClient) {
	//5秒超时
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)
	defer cancel()
	request := &pb.RegisterRequest{
		UserName:"newUser",
		UserPassword:"1",
	}
	r, err := c.UserRegister(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Println("Register success! userId is :", r.UserId)

}

// 用户信息修改
func test_UserUpdate (c pb.UserServiceClient) {
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)	//5秒超时
	defer cancel()
	request := &pb.UserUpdateRequest{
		UserId:"1",
		UserName:"uadateUser",
		UserPassword:"1",
	}
	_, err := c.UserUpdate(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Printf("UserUpdate success!")
}

// 查询用户信息
func test_GetUserInfo (c pb.UserServiceClient) {
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)	//5秒超时
	defer cancel()
	request := &pb.UserId{
		UserId:"1",
	}
	r, err := c.GetUserInfo(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Printf("Get UserInfo success! UserInfo:",r)
}

// 查询用户信息
func test_GetUserScores (c pb.UserServiceClient) {
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)	//5秒超时
	defer cancel()
	r, err := c.GetUserScores(ctx,&pb.Null{})
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Printf("GetUserScores success! UserScores:",r)
}


// 查询垃圾桶状态
func test_GetBinStatus (c pb.UserServiceClient) {
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)	//5秒超时
	defer cancel()
	request := &pb.UserId{
		UserId:"1",
	}
	r, err := c.GetBinStatus(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Printf("GetBinStatus success! BinStatus is:",r.BinStatus)
}

// 查询垃圾桶状态
func test_WasteCount (c pb.UserServiceClient) {
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)	//5秒超时
	defer cancel()
	request := &pb.UserId{
		UserId:"1",
	}
	r, err := c.WasteCount(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Printf("WasteCount success! WasteCount:",r)
}

func testAll(c pb.UserServiceClient) {
	wg := sync.WaitGroup{}
	wg.Add(6)
	go func() {
		test_Register(c)
		wg.Done()
	}()
	go func() {
		test_UserLogin(c)
		wg.Done()
	}()
	go func() {
		test_GetUserInfo(c)
		wg.Done()
	}()
	go func() {
		test_WasteCount(c)
		wg.Done()
	}()
	go func() {
		test_WeekWasteCount(c)
		wg.Done()
	}()
	go func() {
		test_GetBinStatus(c)
		wg.Done()
	}()
	wg.Wait()
}

// 查询垃圾桶状态
func test_WeekWasteCount (c pb.UserServiceClient) {
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*5)	//5秒超时
	defer cancel()
	request := &pb.UserId{
		UserId:"1",
	}
	r, err := c.WeekWasteCount(ctx,request)
	if err != nil {
		log.Fatalf("could not send request: %v", err)
	}
	log.Printf("WeekWasteCount success! WeekWasteCount:",r)
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
	c := pb.NewUserServiceClient(conn)
	//testAll(c)
	test_GetUserScores(c);
}

