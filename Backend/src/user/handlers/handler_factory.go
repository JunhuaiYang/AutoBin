package handlers

import (
	pb "../protos"
	"context"
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"
)
type UserLogin struct {
	user_id 		string
	user_password	string
}
func TestAPI() {

	url := "http://api.choviwu.top/garbage/uploadFile"

	payload := strings.NewReader("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"file\"; filename=\"aaa.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")

	req, _ := http.NewRequest("POST", url, payload)

	req.Header.Add("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
	req.Header.Add("Content-Type", "application/x-www-form-urlencoded,multipart/form-data; boundary=--------------------------063723804965382354942117")
	req.Header.Add("User-Agent", "PostmanRuntime/7.16.3")
	req.Header.Add("Accept", "*/*")
	req.Header.Add("Cache-Control", "no-cache")
	req.Header.Add("Postman-Token", "1ac1fc15-9d64-4505-9991-e7ac56a2eae6,cc31955b-deeb-4ee2-806e-ae6b841bd88d")
	req.Header.Add("Host", "api.choviwu.top")
	req.Header.Add("Accept-Encoding", "gzip, deflate")
	req.Header.Add("Content-Length", "39293")
	req.Header.Add("Connection", "keep-alive")
	req.Header.Add("cache-control", "no-cache")

	res, _ := http.DefaultClient.Do(req)

	defer res.Body.Close()
	body, _ := ioutil.ReadAll(res.Body)

	fmt.Println(res)
	fmt.Println(string(body))

}

// 用户登录
func (*UserServer) UserLogin(ctx context.Context, in *pb.LoginRequest) (*pb.Null, error){
	fmt.Println("user_id", in.UserId, "user_password:",in.UserPassword)
	TestAPI()
	return &pb.Null{},nil
}
// 获取用户信息
func (*UserServer)GetUserInfo(ctx context.Context, in *pb.UserRequest) (ret *pb.UserReply, err error){
	fmt.Println("user_id", in.UserId)
	ret.UserPassword = "2345678"
	ret.UserId = "111"
	return ret,nil
}