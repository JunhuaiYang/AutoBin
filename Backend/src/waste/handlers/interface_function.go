package handlers

import (
	"../conf"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"net/url"
	"runtime"
	"strings"
)

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
	log.Println("获取最高分数的物品名:",maxScore,wasteName)

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
	/// url设置及编码
	v := url.Values{}	// 使用 Encode 进行中文转码
	v.Add("garbageName", wastename)
	config := conf.Config
	url :=config.ClassApiUrl+v.Encode()
	/// req 设置
	req, _ := http.NewRequest("GET", url, nil)
	req.Header.Add("Content-Type", "application/json;charset=UTF-8")
	res, _ := http.DefaultClient.Do(req)
	data, err := ioutil.ReadAll(res.Body)
	if err != nil {
		funcName,file,_,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,err)
		return nil, err
	}

	defer res.Body.Close()
	log.Println("分类请求返回的数据:",string(data))
	/// 请求结果检验
	resData := ClassResults{}
	datajson := []byte(data)
	index := strings.Index(string(data), "code")
	if index >= len(data) || index < 0 {
		funcName,file,_,_ := runtime.Caller(0)
		return  nil, errors.New(runtime.FuncForPC(funcName).Name()+file+"error response get classInfo")
	}
	code := string(data)[index+6:index+9]
	fmt.Println(code)
	if code != "200" {
		funcName,file,_,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,"error code :", code)
		return  nil, err
	}

	err = json.Unmarshal(datajson, &resData)
	if err != nil {
		funcName,file,_,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,err)
		return nil, err
	}
	return &resData, nil
}