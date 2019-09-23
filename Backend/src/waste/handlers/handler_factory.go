package handlers

import (
	"../conf"
	"../db"
	pb "../protos"
	"context"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"net/url"
	"strings"
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
	fmt.Println("BinId", in.BinId, "WasteImage len:",len(in.WasteImage))
	var ret pb.WasteReply
	ret.ResId = -1

	//image := in.WasteImage	// 图片数据
	//buf := new(bytes.Buffer)	// 图片数据
	//writer := multipart.NewWriter(buf)
	//writer.WriteField("sublib", "1")
	//formFile, err := writer.CreateFormFile("file", "./1.jpg")
	//if err != nil {
	//	fmt.Println("Create form file failed: %s\n", err)
	//}
	//srcFile, err := os.Open("G:\\git\\AutoBin\\Backend\\src\\waste\\apple.jpg")
	//if err != nil {
	//	fmt.Println("%Open source file failed: s\n", err)
	//}
	//defer srcFile.Close()
	//_, err = io.Copy(formFile, srcFile)
	//if err != nil {
	//	fmt.Println("Write to form file falied: %s\n", err)
	//}
	//writer.Close() // 发送之前必须调用Close()以写入结尾行
	//image := buf.Bytes()
	//fmt.Println("56",len(image))

	/// 读取本地文件转base64
	//image,_ := ioutil.ReadFile("G:\\git\\AutoBin\\Backend\\src\\waste\\apple.jpg")
	//fmt.Println("image length:",len(image))
	//image_of_base64, err := url.Parse(base64.StdEncoding.EncodeToString(image))	// base64编码
	//if err != nil {
	//	log.Fatal(err)
	//	return &pb.WasteReply{}, err
	//}

	/// 直接接收base64图片
	image := []byte(in.WasteImage)
	image_of_base64, err := url.Parse(in.WasteImage)	// base64编码
	if err != nil {
		log.Fatal(err)
		return &ret, nil
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
		log.Fatal(err)
		return &ret, nil
	}
	log.Println("请求返回的数据:",string(data))
	var resData DetectResult
	err = json.Unmarshal(data, &resData)
	fmt.Println("resData:",resData)
	if err != nil {
		log.Fatal(err)
		return &ret, nil
	}
	/// 调用分类api获取分类信息
	waste_name, type_id, err:= getRes(resData.Result)
	fmt.Println("waste_name:",waste_name, "type_id:",type_id)
	if err != nil {
		log.Fatal("get classInfo error:",err)
		return &ret, nil
	}

	/// 存储信息到数据库 图片，结果
	go func() {
		err := db.AddWaste(waste_name, in.BinId,type_id,image)
		if err != nil {
			fmt.Println("error when store image")
		}
	}()

	ret.ResId = int64(type_id)
	return &ret,nil
}

// 状态上报
func (*WasteServer) BinStatus(ctx context.Context, in *pb.BinStatusRequest) (ret *pb.Null, err error){
	fmt.Println("BinId:", in.BinId, "Status:",in.Status)
	err = db.UpdateBinStatus(int(in.BinId), int(in.Status))
	if err != nil {
		log.Print("BinStatus:", err.Error())
		return ret,err
	}
	return ret,nil
}

// 垃圾桶注册
func (*WasteServer) BinRegister(ctx context.Context, in *pb.BinRegisterRequest) ( *pb.BinRegisterReply, error){
	fmt.Println("UserId:",in.UserId)
	var ret pb.BinRegisterReply
	bin_id, err := db.AddBin(int(in.UserId))
	if err != nil {
		log.Print("BinStatus:", err.Error())
		return &ret,err
	}
	ret.BinId = int32(bin_id)
	return &ret,nil
}

// 获取分类返回的信息
// {"data":[
// {"gname":"苹果","gtype":"湿垃圾"},
// {"gname":"苹果皮","gtype":"湿垃圾"},
// {"gname":"[CQ:at,qq=210039672]苹果核","gtype":"湿垃圾"}],
// "msg":"success",
// "code":200
// }
type ClassItem struct {
	Gname 	string
	Gtype 	string
}
type ClassResults struct {
	Data	[]ClassItem
	Msg 	string
	Code 	int
}

type ErrorClassResults struct {
	Data	string
	Msg 	string
	Code 	int
}

var waste_types = map[string]int {
	"干垃圾":0,
	"湿垃圾":1,
	"可回收":2,
	"不可回收":3,
}

// 解析ResultItem获取物品名称再请求api获取分类信息
func getRes(results []ResultItem)  (string,int,error){
	// {"score": 0.481166, "root": "植物-其它", "keyword": "植物"},
	fmt.Println("130results:",results)
	var maxScore float64
	var wasteName string
	if len(results) == 0 {	// 识别结果为空
		return wasteName,-1, nil
	}
	/// 获取最高分数的物品名
	for i :=0; i < len(results); i++ {
		if results[i].Score > maxScore {
			maxScore = results[i].Score
			wasteName = results[i].Keyword
		}
	}
	fmt.Println("获取最高分数的物品名:",maxScore,wasteName)
	/// 调用分类api获取分类信息
	//config := conf.Config
	//res, err := http.Get(config.ClassApiUrl+wasteName)
	//if err != nil {
	//	log.Print(err)
	//	return wasteName,-1, err
	//}

	resData, err := getClassInfo(wasteName)
	if err != nil {
		return wasteName,-1, err
	}

	/// 获取gtype
	var types_count = make(map[string]int)
	for i :=0; i < len(resData.Data); i++ {
		item := resData.Data[i]
		if item.Gname == wasteName {	// 优先返回同名结果
			return wasteName,waste_types[item.Gtype], nil
		} else {
			if _, ok := types_count[item.Gtype] ; ok {
				types_count[item.Gtype]++;
			} else {
				types_count[item.Gtype] = 1
			}
		}
	}
	// 若无同名结果，则优先返回分类结果中最多的
	waste_type := maxValue(types_count)
	if type_id, ok := waste_types[waste_type] ; ok {
		return wasteName,type_id, nil
	}
	return wasteName,-1, nil
}

// 获取map最大的value，并返回相应的key
func maxValue(types_count map[string]int)  (string){
	maxVal := 0
	var ret string
	for key, val := range types_count {
		if val > maxVal {
			maxVal = val
			ret = key
		}
	}
	return ret
}

// 请求api获取分类信息
func getClassInfo(wastename string )(*ClassResults, error){
	v := url.Values{}
	v.Add("garbageName", wastename)
	wastename1 := v.Encode()	// 中文转码
	config := conf.Config
	url :=config.ClassApiUrl+wastename1
	fmt.Println(url)
	req, _ := http.NewRequest("GET", url, nil)
	req.Header.Add("Content-Type", "application/json;charset=UTF-8")
	res, _ := http.DefaultClient.Do(req)
	data, err := ioutil.ReadAll(res.Body)
	if err != nil {
		log.Fatal(err)
		return nil, err
	}
	defer res.Body.Close()
	log.Println("分类请求返回的数据:",string(data))
	resData := ClassResults{}
	datajson := []byte(data)
	index := strings.Index(string(data), "code:")
	code := string(data)[index+5:3]
	if code != "200" {
		log.Println("error code :", code )
		return  nil, err
	}

	err = json.Unmarshal(datajson, &resData)
	if err != nil {
		log.Println("error classInfo:",err)
		return nil, err
	}
	return &resData, nil
}