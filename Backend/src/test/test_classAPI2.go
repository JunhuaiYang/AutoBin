package main

import (
	"../waste/conf"
	"io/ioutil"
	"log"
	"net/http"
	"net/url"
	"runtime"
)

func main()  {
	config := conf.Config
	v := url.Values{}	// 使用 Encode 进行中文转码
	v.Add("key", config.ClassApiKey)
	v.Add("word", "纸品湿巾")
	url :="http://api.tianapi.com/txapi/lajifenlei/?"+v.Encode()
	/// req 设置
	req, _ := http.NewRequest("GET", url, nil)
	req.Header.Add("Content-Type", "application/json;charset=UTF-8")
	res, _ := http.DefaultClient.Do(req)
	data, err := ioutil.ReadAll(res.Body)
	if err != nil {
		funcName,file,_,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,err)
		return
	}
	log.Println(string(data))
}
