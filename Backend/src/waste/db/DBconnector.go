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
	config := conf.Config
	var err error
	connArgs := fmt.Sprintf("%s:%s@(%s:%s)/%s?charset=utf8&parseTime=True&loc=Local",
		config.UserName,config.Password, config.Host, config.Port,config.DbName )
	fmt.Println("connArgs:",connArgs)
	DB, err = gorm.Open("mysql", connArgs)
	if err != nil {
		log.Fatal(err)
	}
}
