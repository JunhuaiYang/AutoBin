package handlers

import (
	"../conf"
	"../db"
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
	/// 先查询数据库是否有同样物品名
	type_id, err := db.SearchWasteType(wasteName)
	if type_id != -2 {
		return wasteName,type_id, nil
	}
	if err != nil {
		log.Println(err)
	}
	/// 若数据库无相同物品名，则请求分类接口
	/*
	resData, err := getClassInfo1(wasteName)
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
	 */
	/*
	resData, err := getClassInfo2(wasteName)
	if err != nil {
		return wasteName,-1, err
	}

	/// 获取types_count
	var types_count = make(map[string]int)
	for i :=0; i < len(resData.Newslist); i++ {
		item := resData.Newslist[i]
		if item.Name == wasteName {	// 优先返回同名结果
			return wasteName,waste_types[item.Name], nil
		} else {
			if _, ok := types_count[strconv.Itoa(item.Type)] ; ok {
				types_count[strconv.Itoa(item.Type)]++;
			} else {
				types_count[strconv.Itoa(item.Type)] = 1
			}
		}
	}

	// 若无同名结果，则优先返回分类结果中最多的
	waste_type := maxValue(types_count)
	if type_id, ok := waste_types[waste_type] ; ok {
		return wasteName,type_id, nil
	}

	*/
	type_id, err = getWasteType1(wasteName)
	if type_id == -1 {
		type_id, err = getWasteType2(wasteName)
	}
	if err != nil {
		return wasteName, -1, err
	}
	return wasteName,type_id, nil
}

/// 根据wasteName获取垃圾的种类type_id
func getWasteType1(wasteName string) (int, error){
	resData, err := getClassInfo1(wasteName)
	if err != nil {
		return -1, err
	}

	/// 获取gtype
	var types_count = make(map[string]int)
	for i :=0; i < len(resData.Data); i++ {
		item := resData.Data[i]
		if item.Gname == wasteName {	// 优先返回同名结果
			return waste_types[item.Gtype], nil
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
		return type_id, nil
	}
	return -1, nil
}

/// 根据wasteName获取垃圾的种类type_id
func getWasteType2(wasteName string) (int, error){
	resData, err := getClassInfo2(wasteName)
	if err != nil {
		return -1, err
	}

	/// 获取types_count
	//log.Println("获取types_count...len(resData.Newslist):")
	var types_count = [4]int{0,0,0,0}
	type_id := 0
	maxType := 0
	for i :=0; i < len(resData.Newslist); i++ {
		item := resData.Newslist[i]
		log.Println(item.Name)
		if item.Name == wasteName {	// 优先返回同名结果
			return item.Type, nil
		} else {	// 若无同名结果，则优先返回分类结果中最多的
			types_count[item.Type]++
			if types_count[item.Type] > maxType {
				type_id = item.Type
				maxType = types_count[item.Type]
			}
		}
	}
	return type_id, nil
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

/////////////////////////////////////////////////////////////////////

// 获取分类返回的信息
// {"data":[
// {"gname":"苹果","gtype":"湿垃圾"},
// {"gname":"苹果皮","gtype":"湿垃圾"},
// {"gname":"[CQ:at,qq=210039672]苹果核","gtype":"湿垃圾"}],
// "msg":"success",
// "code":200
// }
type ClassItem1 struct {
	Gname 	string
	Gtype 	string
}
type ClassResults1 struct {
	Data	[]ClassItem1
	Msg 	string
	Code 	int
}
type ErrorClassResults1 struct {
	Data	string
	Msg 	string
	Code 	int
}

// 请求api获取分类信息
func getClassInfo1(wastename string )(*ClassResults1, error){
	/// url设置及编码
	v := url.Values{}	// 使用 Encode 进行中文转码
	v.Add("garbageName", wastename)
	config := conf.Config
	url :=config.ClassApiUrl1+v.Encode()
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
	resData := ClassResults1{}
	datajson := []byte(data)
	index := strings.Index(string(data), "code")
	if index >= len(data) || index < 0 {
		funcName,file,_,_ := runtime.Caller(0)
		errors.New("user not found")
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

/////////////////////////////////////////////////////////////////////

// {
//"code":200,
//"msg":"success",
//"newslist":[
//{
//"name":"隐形眼镜",
//"type":3,
//"aipre":0,
//"explain":"干垃圾即其它垃圾，指除可回收物、有害垃圾、厨余垃圾（湿垃圾）以外的其它生活废弃物。",
//"contain":"常见包括砖瓦陶瓷、渣土、卫生间废纸、猫砂、污损塑料、毛发、硬壳、一次性制品、灰土、瓷器碎片等难以回收的废弃物",
//"tip":"尽量沥干水分；难以辨识类别的生活垃圾都可以投入干垃圾容器内"
//},
//{
//"name":"眼镜",
//"type":3,
//"aipre":0,
//"explain":"干垃圾即其它垃圾，指除可回收物、有害垃圾、厨余垃圾（湿垃圾）以外的其它生活废弃物。",
//"contain":"常见包括砖瓦陶瓷、渣土、卫生间废纸、猫砂、污损塑料、毛发、硬壳、一次性制品、灰土、瓷器碎片等难以回收的废弃物",
//"tip":"尽量沥干水分；难以辨识类别的生活垃圾都可以投入干垃圾容器内"
//},
//{
//"name":"智能眼镜",
//"type":0,
//"aipre":0,
//"explain":"可回收垃圾是指适宜回收、可循环利用的生活废弃物。",
//"contain":"常见包括各类废金属、玻璃瓶、易拉罐、饮料瓶、塑料玩具、书本、报纸、广告单、纸板箱、衣服、床上用品、电子产品等",
//"tip":"轻投轻放；清洁干燥，避免污染，费纸尽量平整；立体包装物请清空内容物，清洁后压扁投放；有尖锐边角的、应包裹后投放"
//}
//]
//}
type ClassItem2 struct {
	Name 	string
	Type 	int
	Aipre	int
	Explain	string
	Contain	string
	Tip		string
}
type ClassResults2 struct {
	Newslist	[]ClassItem2
	Msg 	string
	Code 	int
}
type ErrorClassResults2 struct {
	Msg 	string
	Code 	int
}

// 请求api获取分类信息
func getClassInfo2(wastename string )(*ClassResults2, error){
	config := conf.Config
	/// url设置及编码
	v := url.Values{}	// 使用 Encode 进行中文转码
	v.Add("key", config.ClassApiKey)
	v.Add("word", "顶花灯")
	url :=config.ClassApiUrl2 + v.Encode()
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
	resData := ClassResults2{}
	datajson := []byte(data)
	index := strings.Index(string(data), "code")
	if index >= len(data) || index < 0 {
		funcName,file,_,_ := runtime.Caller(0)
		return  nil, errors.New(runtime.FuncForPC(funcName).Name()+file+"error response get classInfo")
	}
	code := string(data)[index+6:index+9]
	if code != "200" {
		funcName,file,_,_ := runtime.Caller(0)
		err = errors.New(runtime.FuncForPC(funcName).Name()+file+"error code :"+code)
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

