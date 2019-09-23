package util

import (
	"encoding/base64"
	"errors"
	"log"
	"regexp"
)


//  将接收到的base64 的图片数据转为byte，返回byte与图片类型
func GetImage(path string, base64_image_content string) ([]byte, string,error) {
	b, _ := regexp.MatchString(`^data:\s*image\/(\w+);base64,`, base64_image_content)
	if !b {
		return nil, "",errors.New("image base64Str doesn't contain a prefix")
	}
	// "data:image/png;base64,................"
	pre, _ := regexp.Compile(`^data:\s*image\/(\w+);base64,`)	// 正则匹配，返回匹配字符串
	allData := pre.FindAllSubmatch([]byte(base64_image_content), 2)
	fileType := string(allData[0][1]) //png ，jpeg 后缀获取
	base64Str := pre.ReplaceAllString(base64_image_content, "")	// 去掉前缀
	imageByte, err := base64.StdEncoding.DecodeString(base64Str)
	if err != nil {
		log.Fatal(err)
		return nil, "",err
	}
	return imageByte,fileType,err
}
