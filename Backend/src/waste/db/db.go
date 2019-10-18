package db

import (
	"fmt"
	"log"
	"runtime"
	"strconv"
	"time"
)

type User struct {
	User_id 		string	`gorm:"column:user_id;primary_key;AUTO_INCREMENT"`
	User_name		string	`gorm:"column:name;primary_key;"`
	Password		string 	`gorm:"column:password;"`
	Score			int		`gorm:"column:score;"`
}

type Waste struct {
	Waste_id 		int		`gorm:"column:id;primary_key;AUTO_INCREMENT"`
	Bin_id			int		`gorm:"column:bin_id;"`
	Waste_name		string	`gorm:"column:waste_name;"`
	Create_time		int 	`gorm:"column:create_time;"`
	Type_id			int		`gorm:"column:type_id;"`
	Image			[]byte	`gorm:"column:image;"`
}

type Bin struct {
	Bin_id		int		`gorm:"column:bin_id;primary_key;AUTO_INCREMENT"`
	Status 		int		`gorm:"column:status;"`
	Start_time 	int		`gorm:"column:start_time;"`
	Ip_address	string	`gorm:"column:ip_address;"`
	Angel 		float32	`gorm:"column:angel;"`
	Temp		float32 `gorm:"column:temp;"`
	Comments	string 	`gorm:"column:comment;"`
}

type UserBinRelation struct {
	Id 			int `gorm:"column:id;primary_key;AUTO_INCREMENT"`
	Bin_id		int	`gorm:"column:bin_id;"`
	User_id		int `gorm:"column:user_id;"`
}

type BinWasteRelation struct {
	Id 			int `gorm:"column:id;primary_key;AUTO_INCREMENT"`
	Bin_id		int	`gorm:"column:bin_id"`
	Waste_id	int `gorm:"column:waste_id"`
}

// 添加垃圾信息 wastes
func AddWaste(waste_name string, bin_id string,type_id int, image []byte ) (error) {
	log.Println("AddWaste(",waste_name, bin_id, type_id,")")
	db := DB
	/// 创建新的垃圾记录
	newWaste := Waste{}
	newWaste.Bin_id, _ = strconv.Atoi(bin_id)
	newWaste.Waste_name = waste_name
	newWaste.Image = image
	newWaste.Create_time = int(time.Now().Unix())
	newWaste.Type_id = type_id
	tx := db.Begin()
	dbret := tx.Create(&newWaste)
	if dbret.Error != nil {
		log.Println(dbret.Error)
		return dbret.Error
	}

	/// 创建新的 bin_waste_relation 记录
	var newBinWasteRelation BinWasteRelation
	newBinWasteRelation.Bin_id, _ = strconv.Atoi(bin_id)
	newBinWasteRelation.Waste_id = newWaste.Waste_id
	//fmt.Println(newBinWasteRelation.bin_id,newBinWasteRelation.waste_id)
	dbret = tx.Create(&newBinWasteRelation)
	if dbret.Error != nil {
		tx.Rollback()
		return dbret.Error
	}

	/// 修改用户积分
	var userBinRelation UserBinRelation
	//bin_id1,_ := strconv.Atoi(bin_id)
	dbret = db.Table("user_bin_relations").Where("bin_id = ?",bin_id ).First(&userBinRelation)
	if dbret.Error != nil {	// user_bin_relation 记录不存在
		tx.Rollback()
		funcName,file,_,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,dbret.Error)
		return dbret.Error
	}
	var user User
	dbret = db.Where("user_id = ?",userBinRelation.User_id).Find(&user)
	if dbret.Error != nil {	// user_bin_relation 记录不存在
		log.Println(dbret.Error.Error())
		tx.Rollback()
		return dbret.Error
	}
	user.Score = user.Score + 1
	dbret = tx.Model(&User{}).Where("user_id = ? ", user.User_id).Update("score",user.Score);
	if dbret.Error != nil {
		fmt.Println("score",dbret.Error.Error())
		tx.Rollback()
		return dbret.Error
	}
	tx.Commit()

	log.Println("success!!!")
	return nil
}

// 修改用户积分 users
func UpdateUserScore(user_id int, score int) (error) {
	db := DB
	var user User
	db.Where("user_id = ?", user_id).Find(&user)
	user.Score = user.Score + score
	tx := db.Begin()
	dbret := tx.Model(&User{}).Where("user_id = ? ", user_id).Update("score",user.Score);
	if dbret.Error != nil {
		tx.Rollback()
		return dbret.Error
	}
	tx.Commit()
	return nil
}

// 添加新垃圾桶信息	bin & bin_user_relations
func AddBin(user_id int) (int , error) {
	db := DB
	var newBin Bin
	var newUserBin UserBinRelation
	newBin.Status = 1
	newBin.Comments = "启动"
	newBin.Start_time = (int)(time.Now().Unix())
	fmt.Println("newBin:",newBin.Start_time)
	/// 创建新的垃圾桶记录
	tx := db.Begin()
	dbret := tx.Create(&newBin)
	if dbret.Error != nil {
		tx.Rollback()
		return -1,dbret.Error
	}
	/// 创建新的垃圾桶与用户关联记录
	newUserBin.Bin_id = newBin.Bin_id// newBin.bin_id
	newUserBin.User_id = user_id
	dbret = tx.Table("user_bin_relations").Create(&newUserBin)
	if dbret.Error != nil {
		tx.Rollback()
		return -1, dbret.Error
	}
	tx.Commit()
	return newBin.Bin_id, nil
}

// 修改垃圾桶状态  bins
func UpdateBinStatus(bin_id int, status_id int, angel float32, term float32) (error) {
	db := DB
	//var bin Bin
	//db.Where("bin_id = ?", bin_id).Find(&bin)
	tx := db.Begin()
	dbret := tx.Model(&Bin{}).Where("bin_id = ?", bin_id).Update(
		map[string]interface{}{"status":status_id, "angel":angel, "term":term })
	if dbret.Error != nil {
		tx.Rollback()
		return dbret.Error
	}
	tx.Commit()
	return nil
}

// 查询waste的type_id
func SearchWasteType(waste_name string) (int, error) {
	log.Println("SearchWasteType(waste_name):",waste_name)
	var waste Waste
	count := 0
	db := DB
	db.Model(&Waste{}).Where(" waste_name = ? ", waste_name).Find(&waste).Count(&count)
	if count == 0 {
		return -2, nil
	}
	if db.Error != nil {
		return -2, db.Error
	}
	return waste.Type_id, nil
}
