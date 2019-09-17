package main

import (
	"./conf"
	"./handlers"
	pb "./protos"
	"context"
	"fmt"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"github.com/rs/cors"
	"github.com/tmc/grpc-websocket-proxy/wsproxy"
	"google.golang.org/grpc"
	"log"
	"net"
	"net/http"
	"strings"
	"sync"
)

func startGrpcServer() (error) {
	var config = conf.Config
	/// 开启Grpc端口监听
	lis, err := net.Listen("tcp", fmt.Sprintf("%s", config.Host + ":" + config.GrpcPort))
	if err != nil {
		return err
	}

	grpcServer := grpc.NewServer()	// 获取grpcServer
	pb.RegisterUserServiceServer(grpcServer, handlers.NewService())	//
	return grpcServer.Serve(lis)
}

func startHttpServer() error {
	// fileServer
	var config = conf.Config
	verify := config.Verify
	fileServer := http.StripPrefix("/autobin/user/statics/", http.FileServer(http.Dir(verify)))

	ctx := context.Background()
	ctx, cancel :=context.WithCancel(ctx)
	defer  cancel()

	mux := runtime.NewServeMux()
	opts := []grpc.DialOption{grpc.WithInsecure()}
	err := pb.RegisterUserServiceHandlerFromEndpoint(ctx, mux, config.GrpcEndpoint, opts)
	if err != nil {
		fmt.Println(48,err.Error())
		return err
	}

	c := cors.New(cors.Options{
		AllowedOrigins: []string{"*"},
		AllowedMethods:[]string{
			http.MethodDelete,
			http.MethodOptions,
			http.MethodPost,
			http.MethodGet,
			http.MethodHead,
			http.MethodPatch,
			http.MethodPut,
		},
		AllowedHeaders:[]string{"*"},
		AllowCredentials:false,
	})
	handler := c.Handler(mux)
	return http.ListenAndServe(fmt.Sprintf("%s",config.Host+":"+config.HttpPort),setFileServer(fileServer, wsproxy.WebsocketProxy(handler)))
}

func setFileServer(fileServer, other http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		config := conf.Config
		prefix := config.Prefix
		if len(prefix) > 1 {
			prefix = prefix[:len(prefix) - 1]
		}
		if strings.HasPrefix(r.RequestURI, prefix+"/statics/"){
			fileServer.ServeHTTP(w,r)
		} else {
			other.ServeHTTP(w,r)
		}
	})
}

func main(){
	wg := new (sync.WaitGroup)
	wg.Add(2)

	go func() {
		err := startGrpcServer()
		if err != nil {
			log.Fatal("startGrpcServer:", err)
		}
		wg.Done()
	}()

	go func() {
		err := startHttpServer()
		if err != nil {
			log.Fatal("startHttpServer:",err)
		}
		wg.Done()
	}()
	wg.Wait()
}

//func main() {
//
//	url := "http://api.choviwu.top/garbage/uploadFile"
//	buf := new(bytes.Buffer)	// 图片数据
//	writer := multipart.NewWriter(buf)
//	writer.WriteField("sublib", "1")
//	formFile, err := writer.CreateFormFile("file", "./1.jpg")
//	if err != nil {
//		fmt.Println("Create form file failed: %s\n", err)
//	}
//	srcFile, err := os.Open("G:\\git\\AutoBin\\Backend\\src\\user\\apple.jpg")
//	if err != nil {
//		fmt.Println("%Open source file failed: s\n", err)
//	}
//	defer srcFile.Close()
//	_, err = io.Copy(formFile, srcFile)
//	if err != nil {
//		fmt.Println("Write to form file falied: %s\n", err)
//	}
//	writer.Close() // 发送之前必须调用Close()以写入结尾行
//	var imageData io.Reader
//	imageData.Read(buf.Bytes())
//	req, _ := http.NewRequest("POST", url, imageData)
//	req.Header.Add("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
//	//req.Header.Add("User-Agent", "PostmanRuntime/7.16.3")
//	req.Header.Add("Accept", "*/*")
//	//req.Header.Add("Cache-Control", "no-cache")
//	//req.Header.Add("Postman-Token", "eb1a49ee-99e7-487e-9805-4da100d65ca5,cd993124-9c2f-4b95-bee2-76f030933244")
//	//req.Header.Add("Host", "api.choviwu.top")
//	req.Header.Add("Content-Type", "multipart/form-data; boundary=--------------------------153120684342055966849553")
//	req.Header.Add("Accept-Encoding", "gzip, deflate")
//	//req.Header.Add("Content-Length", "4447")
//	//req.Header.Add("Connection", "keep-alive")
//	//req.Header.Add("cache-control", "no-cache")
//
//	res, _ := http.DefaultClient.Do(req)
//
//	defer res.Body.Close()
//	body, _ := ioutil.ReadAll(res.Body)
//
//	fmt.Println(res)
//	fmt.Println(string(body))
//}