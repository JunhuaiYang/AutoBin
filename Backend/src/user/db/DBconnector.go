package db

import (
	"../conf"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
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
	//var config = DBconfig{
	//	"root",
	//	"123456",
	//	"127.0.0.1",
	//	"3306",
	//	"user",
	//}
	config := conf.Config
	var err error
	connArgs := fmt.Sprintf("%s:%s@(%s:%s)/%s?charset=utf8&parseTime=True&loc=Local",
		config.UserName,config.Password, config.Host, config.Port,config.DbName )
	DB, err = gorm.Open("mysql", connArgs)
	if err != nil {
		log.Fatal(err)
	}
	log.Println()
}
