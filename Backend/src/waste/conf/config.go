package conf

import (
	"encoding/json"
	"fmt"
	io "io/ioutil"
	"log"
	"os"
	"sync"
)

//定义配置文件解析后的结构
type ConfigInfo struct {
	UserName  		string `json:userName`
	Password  		string `json:password`
	Host      		string `json:host`
	Port  	  		string `json:port`
	DbName    		string `json:dbName`
	GrpcPort		string `json:grpcPort`
	HttpPort		string `json:httpPort`
	Verify			string `json:verify`
	Prefix			string `json:prefix`
	GrpcEndpoint	string `json:grpcEndpoint`
	HttpEndpoint 	string `json:httpEndpoint`
	HttpHost		string `json:httpHost`

	AppKey			string `json:appKey`		// 百度云应用键值
	Secret			string `json:secret`		// 百度云应用秘钥
	AccessToken		string `json:accessToken`	// 调api使用的token
	ApiUrl			string `json:apiUrl`		// api路径 获取物品名
	ClassApiUrl1	string `json:ClassApiUrl1`	// api路径1 获取分类

	ClassApiUrl2	string `json:ClassApiUrl2`	// api路径2 获取分类
	ClassApiKey		string `json:classApiKey`	// 用户自己的APIKEY
}

var Config ConfigInfo
var file_locker sync.Mutex //文件锁

// 初始化文件配置
func init()  {
	conf, err := LoadConfig("\\config.json") //get config struct
	if err != nil {
		log.Fatal("InitConfig failed:", err.Error())
	}
	Config = conf
}

// 加载配置文件
func LoadConfig(filename string) (ConfigInfo, error) {
	var conf ConfigInfo
	file_locker.Lock()
	data, err := io.ReadFile(getCurrentPath()+filename) //read config file
	file_locker.Unlock()
	if err != nil {
		fmt.Println("read json file error")
		return conf, err
	}
	datajson := []byte(data)
	err = json.Unmarshal(datajson, &conf)
	if err != nil {
		fmt.Println("unmarshal json file error")
		return conf, err
	}
	return conf, nil
}


//获取当前路径，比如：E:/abc/data/test
func getCurrentPath() string {
	dir, err := os.Getwd()
	if err != nil {
		log.Fatal(err)
	}
	dir += "\\Backend\\src\\waste\\conf"
	return dir
}