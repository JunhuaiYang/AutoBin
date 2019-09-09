package orm

import (
	"fmt"
	"github.com/jinzhu/gorm"
	"log"
)

type DBconfig struct{
	User 		string
	Password	string
	Host		string
	Port		string
	DBname		string
}

var DB *gorm.DB

func init(){
	var config = DBconfig{
		"root",
		"123456",
		"127.0.0.1",
		"3306",
		"user",
	}
	var err error
	connArgs := fmt.Sprintf("%s:%s@(%s:%s)/%s?charset=utf8&parseTime=True&loc=Local",
		config.User,config.Password, config.Host, config.Port,config.DBname )
	DB, err = gorm.Open("mysql", connArgs)
	if err != nil {
		log.Fatal(err)
	}
}
