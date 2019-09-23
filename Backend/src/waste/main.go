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
	lis, err := net.Listen("tcp", fmt.Sprintf("%s", config.HttpHost + ":" + config.GrpcPort))
	if err != nil {
		return err
	}

	grpcServer := grpc.NewServer()
	pb.RegisterWasteServiceServer(grpcServer, handlers.NewService())
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
	err := pb.RegisterWasteServiceHandlerFromEndpoint(ctx, mux, config.GrpcEndpoint, opts)
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
	return http.ListenAndServe(fmt.Sprintf("%s",config.HttpHost+":"+config.HttpPort),setFileServer(fileServer, wsproxy.WebsocketProxy(handler)))
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