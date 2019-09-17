package handlers
import (
	"../conf"
	"../db"
	pb "../protos"
	"context"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"net/url"
)
// 获取物品名请求返回的数据:
// {"log_id": 1207738314010879151, "result_num": 5, "result":
// [{"score": 0.481166, "root": "植物-其它", "keyword": "植物"},
// {"score": 0.006152, "root": "植物-蔷薇科", "keyword": "草莓"}]}
type ResultItem struct {
	score		float64
	root		string
	keyword		string
}
type DetectResult struct {
	log_id 		int64
	result_num	int
	results		[]ResultItem
}

// 垃圾检测
func (*WasteServer) WasteDetect(ctx context.Context, in *pb.WasteRequest) (ret *pb.WasteReply,err error){
	fmt.Println("BinId", in.BinId, "WasteImage:",in.WasteImage)
	image := in.WasteImage	// 图片数据
	image_of_base64, err := url.Parse(base64.StdEncoding.EncodeToString(image))	// base64编码
	if err != nil {
		log.Fatal(err)
		return &pb.WasteReply{}, err
	}
	config := conf.Config
	api_url := config.ApiUrl+"?access_token="+config.AccessToken
	values := url.Values{}	// map[string][]string, key:string, value:[]string
	values.Add("image", image_of_base64.EscapedPath())
	values.Add("multi_detect", "false")
	res, err := http.PostForm(api_url, values)	// 发送请求获取应答数据
	defer res.Body.Close()
	if err != nil {
		log.Fatal(err)
		return &pb.WasteReply{}, err
	}
	data, err := ioutil.ReadAll(res.Body)
	if err != nil {
		log.Fatal(err)
		return &pb.WasteReply{}, err
	}
	log.Println("请求返回的数据:",string(data))
	resData := DetectResult{}
	err = json.Unmarshal(data, &resData)
	if err != nil {
		log.Fatal(err)
		return  &pb.WasteReply{}, err
	}
	waste_name, type_id, err:= getRes(resData.results)
	if err != nil {
		log.Fatal(err)
		return  &pb.WasteReply{}, err
	}
	/// 存储信息到数据库 图片，结果
	go func() {
		db.AddWaste(waste_name, in.BinId,type_id,in.WasteImage)
	}()
	ret.ResId = int64(type_id)
	return ret,nil
}
// 检测垃圾桶状态
func (*WasteServer) BinStatus(ctx context.Context, in *pb.BinStatusRequest) (ret *pb.Null, err error){
	fmt.Println("WasteId:", in.WasteId, "Status:",in.Status)

	return ret,nil
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
	gname 	string
	gtype 	string
}
type ClassResults struct {
	data	[]ClassItem
	msg 	string
	code 	int
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
	var maxScore float64
	var wasteName string
	if len(results) == 0 {	// 识别结果为空
		return wasteName,-1, nil
	}
	/// 获取最高分数的物品名
	for i :=0; i < len(results); i++ {
		if results[i].score > maxScore {
			maxScore = results[i].score
			wasteName = results[i].keyword
		}
	}
	/// 调用分类api获取分类信息
	config := conf.Config
	res, err := http.Get(config.ClassApiUrl+wasteName)
	if err != nil {
		log.Print(err)
		return wasteName,-1, err
	}
	data, err := ioutil.ReadAll(res.Body)
	if err != nil {
		log.Fatal(err)
		return wasteName,-1, err
	}
	log.Println("分类请求返回的数据:",string(data))
	resData := ClassResults{}
	err = json.Unmarshal(data, &resData)
	if err != nil {
		log.Println(err)
		return wasteName,-1, err
	} else if resData.code != 200 {
		log.Println("error code :", resData.code )
		return  wasteName,-1, err
	}

	/// 获取gtype
	var types_count = make(map[string]int)
	for i :=0; i < len(resData.data); i++ {
		item := resData.data[i]
		if item.gname == wasteName {	// 优先返回同名结果
			return wasteName,waste_types[item.gtype], nil
		} else {
			if _, ok := types_count[item.gtype] ; ok {
				types_count[item.gtype]++;
			} else {
				types_count[item.gtype] = 1
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