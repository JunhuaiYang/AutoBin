package db

import (
	"fmt"
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
	Waste_name		string	`gorm:"column:waste_name;"`
	Create_time		string 	`gorm:"column:create_time;"`
	Type_id			int		`gorm:"column:type_id;"`
	Image			[]byte	`gorm:"column:image;"`
}

type Bin struct {
	bin_id		int		`gorm:"column:bin_id;primary_key;AUTO_INCREMENT"`
	status 		int		`gorm:"column:status;"`
	comments	string 	`gorm:"column:comments;"`
	start_time string	`gorm:"column:start_time;"`
}

type UserBinRelation struct {
	id 			int `gorm:"column:id;primary_key;AUTO_INCREMENT"`
	bin_id		int	`gorm:"column:bin_id;"`
	user_id		int `gorm:"column:user_id;"`
}

type BinWasteRelation struct {
	id 			int `gorm:"column:id;primary_key;AUTO_INCREMENT"`
	bin_id		int	`gorm:"column:bin_id;"`
	waste_id	int `gorm:"column:waste_id;"`
}

// 添加垃圾信息 waste
func AddWaste(waste_name string, bin_id string,type_id int, image []byte ) (error) {
	fmt.Println("AddWaste(",waste_name, bin_id, type_id,")")
	db := DB
	/// 创建新的垃圾记录
	newWaste := Waste{}
	newWaste.Bin_id, _ = strconv.Atoi(bin_id)
	newWaste.Waste_name = waste_name
	newWaste.Image = image
	newWaste.Create_time = time.Now().String()
	newWaste.Type_id = type_id
	tx := db.Begin()
	dbret := tx.Create(&newWaste)
	if dbret.Error != nil {
		return dbret.Error
	}

	/// 创建新的 bin_waste_relation 记录
	var newBinWasteRelation BinWasteRelation
	newBinWasteRelation.bin_id, _ = strconv.Atoi(bin_id)
	newBinWasteRelation.waste_id = newWaste.Waste_id
	fmt.Println("newBinWasteRelation.bin_id:", newBinWasteRelation.bin_id)
	dbret = tx.Create(&newBinWasteRelation)
	if dbret.Error != nil {
		return dbret.Error
	}

	/// 修改用户积分
	var userBinRelation UserBinRelation
	dbret = db.Where("bin_id = ?", bin_id).Find(&userBinRelation)
	if dbret.Error != nil {	// user_bin_relation 记录不存在
		tx.Rollback()
		log.Fatal(dbret.Error)
		return dbret.Error
	}
	var user User
	dbret = db.Where("user_id = ?",userBinRelation.user_id).Find(&user)
	if dbret.Error != nil {	// user_bin_relation 记录不存在
		tx.Rollback()
		log.Fatal(dbret.Error)
		return dbret.Error
	}
	user.score = user.score + 1
	dbret = tx.Model(&User{}).Where("user_id = ? ", user.user_id).Update("score",user.score);
	if dbret.Error != nil {
		tx.Rollback()
		return dbret.Error
	}
	tx.Commit()

	fmt.Println("success!!!")
	return nil
}

// 修改用户积分 user
func UpdateUserScore(user_id int, score int) (error) {
	db := DB
	var user User
	db.Where("user_id = ?", user_id).Find(&user)
	user.score = user.score + score
	tx := db.Begin()
	dbret := tx.Model(&User{}).Where("user_id = ? ", user_id).Update("score",user.score);
	if dbret.Error != nil {
		tx.Rollback()
		return dbret.Error
	}
	tx.Commit()
	return nil
}

// 添加新垃圾桶信息	bin & bin_user_relation
func AddBin(user_id int) (int , error) {
	db := DB
	var newBin Bin
	var newUserBin UserBinRelation
	newBin.status = 0
	newBin.comments = ""
	newBin.start_time = time.Now().String()
	/// 创建新的垃圾桶记录
	tx := db.Begin()
	dbret := tx.Create(newBin)
	if dbret.Error != nil {
		tx.Rollback()
		return -1,dbret.Error
	}
	/// 创建新的垃圾桶与用户关联记录
	newUserBin.bin_id = newBin.bin_id
	newUserBin.user_id = user_id
	dbret = tx.Create(newUserBin)
	if dbret.Error != nil {
		tx.Rollback()
		return -1, dbret.Error
	}
	tx.Commit()
	fmt.Println("add bin success!!!")
	return newBin.bin_id, nil
}

// 修改垃圾桶状态  bin
func UpdateBinStatus(bin_id int, status_id int) (error) {
	db := DB
	var bin Bin
	db.Where("bin_id = ?", bin_id).Find(&bin)
	if bin.status != status_id {
		tx := db.Begin()
		dbret := tx.Model(&Bin{}).Where("bin_id = ?", bin_id).Update("status",status_id)
		if dbret.Error != nil {
			tx.Rollback()
			return dbret.Error
		}
		tx.Commit()
	}
	return nil
}
