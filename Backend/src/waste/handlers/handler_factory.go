package handlers

import (
	"../conf"
	"../db"
	pb "../protos"
	"context"
	"encoding/base64"
	_ "encoding/base64"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"net/url"
	"runtime"
)
// 获取物品名请求返回的数据:
// {"log_id": 1207738314010879151, "result_num": 5, "result":
// [{"score": 0.481166, "root": "植物-其它", "keyword": "植物"},
// {"score": 0.006152, "root": "植物-蔷薇科", "keyword": "草莓"}]}
type ResultItem struct {
	Score		float64	`json:"score"`
	Root		string	`json:"root"`
	Keyword		string	`json:"keyword"`
}
type DetectResult struct {
	Log_id 		int64			`json:"log_id"`
	Result_num	int64			`json:"result_num"`
	Result		[]ResultItem	`json:"result"`
}

// 垃圾检测
func (*WasteServer) WasteDetect(ctx context.Context, in *pb.WasteRequest) (*pb.WasteReply,error){
	log.Println("垃圾检测请求：BinId", in.BinId, "WasteImage len:",len(in.WasteImage))
	var ret pb.WasteReply
	var image []byte
	var image_of_base64 *url.URL
	var err error
	ret.ResId = -1

	if len(in.WasteImage) < 100 {	// 读取本地文件转base64
		image,_ = ioutil.ReadFile("G:\\git\\AutoBin\\Backend\\src\\waste\\test1.jpg")
		log.Println("image length:",len(image))
		image_of_base64, err = url.Parse(base64.StdEncoding.EncodeToString(image))	// base64编码
		if err != nil {
			log.Fatal(err)
			return &pb.WasteReply{}, err
		}
	} else {
		// 直接接收base64图片
		image = []byte(in.WasteImage)
		image_of_base64, err = url.Parse(in.WasteImage)	// base64编码
		if err != nil {
			log.Fatal(err)
			return &ret, nil
		}
	}

	/// 调用百度api识别图片
	config := conf.Config
	api_url := config.ApiUrl+"?access_token="+config.AccessToken
	values := url.Values{}	// map[string][]string, key:string, value:[]string
	values.Add("image", image_of_base64.EscapedPath())
	values.Add("multi_detect", "false")
	res, err := http.PostForm(api_url, values)	// 发送请求获取应答数据
	if err != nil {
		log.Fatal(err)
		return &ret, nil
	}
	defer res.Body.Close()
	data, err := ioutil.ReadAll(res.Body)
	if err != nil {
		funcName,file,line,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,line,err)
		return &ret, nil
	}
	log.Println("图像识别结果:",string(data))
	var resData DetectResult	// 识别结果数据
	err = json.Unmarshal(data, &resData)	// 解析Json数据
	if err != nil {
		funcName,file,line,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,line,err)
		return &ret, nil
	}
	/// 调用分类api获取分类信息
	waste_name, type_id, err:= getRes(resData.Result)
	ret.WasteName = waste_name
	log.Println("垃圾分类结果: ",waste_name, ":",type_id)
	/// 存储信息到数据库 图片，结果
	go func() {
		err := db.AddWaste(waste_name, in.BinId,type_id,image)
		if err != nil {
			funcName,file,line,_ := runtime.Caller(0)
			log.Println(  runtime.FuncForPC(funcName).Name(),file,line,err)
		}
	}()
	if err != nil {
		funcName,file,line,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,line,err)
		return &ret, nil
	}
	ret.ResId = int64(type_id)
	log.Println("ret:",ret.ResId,ret.WasteName)
	return &ret,nil
}

// 状态上报
func (*WasteServer) BinStatus(ctx context.Context, in *pb.BinStatusRequest) (ret *pb.Null, err error){
	fmt.Println("状态上报请求：BinId:", in.BinId, " Status:",in.Status)
	err = db.UpdateBinStatus(int(in.BinId), int(in.Status),in.Angel,in.Temp)
	if err != nil {
		funcName,file,line,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,line,err)
		return ret,err
	}
	ret = new(pb.Null)
	return ret,nil
}

// 垃圾桶注册
func (*WasteServer) BinRegister(ctx context.Context, in *pb.BinRegisterRequest) ( *pb.BinRegisterReply, error){
	fmt.Println("垃圾桶注册请求：UserId:",in.UserId,"BinId:",in.BinId, "in.IpAddress:",in.IpAddress)
	var ret pb.BinRegisterReply
	if in.BinId >= 0 {
		ret.BinId = in.BinId
		return &ret,nil
	}
	bin_id, err := db.AddBin(int(in.UserId))
	if err != nil {
		funcName,file,line,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,line,err)
		return &ret,err
	}
	ret.BinId = int32(bin_id)
	return &ret,nil
}
