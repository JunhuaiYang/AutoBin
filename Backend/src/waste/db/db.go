package db

import (
	"log"
	"strconv"
	"time"
)

type User struct {
	user_id 		string	`gorm:"column:user_id;primary_key;AUTO_INCREMENT"`
	user_name		string	`gorm:"column:name;primary_key;"`
	password		string 	`gorm:"column:password;"`
	score			int		`gorm:"column:score;"`
}

type Waste struct {
	Waste_id 		int		`gorm:"column:id;primary_key;AUTO_INCREMENT"`
	Bin_id			int		`gorm:"column:bin_id;"`
	Waste_name		string	`gorm:"column:name;"`
	Create_time		string 	`gorm:"column:create_time;"`
	Type_id			int		`gorm:"column:score;"`
	Image			string	`gorm:"column:image;"`
}

type Bin struct {
	id 			int `gorm:"column:id;primary_key;AUTO_INCREMENT"`
	bin_id		int	`gorm:"column:bin_id;"`
	status 		int	`gorm:"column:status;"`
	user_id		int `gorm:"column:user_id;"`
}

// 添加垃圾信息
func AddWaste(waste_name string, bin_id string,type_id int, image []byte ) (error) {
	db := DB
	newWaste := Waste{}
	newWaste.Bin_id, _ = strconv.Atoi(bin_id)
	newWaste.Waste_name = waste_name
	newWaste.Image = string(image)
	newWaste.Create_time = time.Now().String()
	newWaste.Type_id = type_id
	/// 创建新的垃圾记录
	tx := db.Begin()
	dbret := tx.Create(&newWaste)
	if dbret.Error != nil {
		return dbret.Error
	}
	/// 修改用户积分
	var bin Bin
	dbret = db.Model(&Bin{}).Where("bin_id = ?", bin_id).Find(&bin)
	if dbret.Error != nil {
		tx.Rollback()
		log.Fatal(dbret.Error)
		return dbret.Error
	}
	var user User
	dbret = db.Model(&User{}).Where("user_id = ?", bin.user_id).Find(&user)
	if dbret.Error != nil {
		tx.Rollback()
		log.Fatal(dbret.Error)
		return dbret.Error
	}
	dbret = tx.Model(&User{}).Where("user_id = ? ", user.user_id).Update("score",user.score+1);
	if dbret.Error != nil {
		tx.Rollback()
		log.Fatal(dbret.Error)
		return dbret.Error
	}
	return nil
}

// 修改垃圾桶状态
func UpdateUer(bin_id int, status_id int) (error) {
	db := DB
	dbret := db.Model(&Bin{}).Where("bin_id = ?", bin_id).Update("status",status_id);
	if dbret.Error != nil {
		return dbret.Error
	}
	return nil
}

// 修改用户积分
func UpdateUserScore(user_id string, score int) (error) {
	db := DB
	dbret := db.Model(&User{}).Where("user_id = ? ", user_id).Update("score",score);
	if dbret.Error != nil {
		return dbret.Error
	}
	return nil
}