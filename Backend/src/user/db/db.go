package db

import (
	"fmt"
	"log"
	"runtime"
	"time"
	pb "../protos"
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
	Create_time		string 	`gorm:"column:create_time;"`
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
	Bin_id		int	`gorm:"column:bin_id;"`
	Waste_id	int `gorm:"column:waste_id;"`
}

// 添加用户
func AddUer(user_name string, user_password string) (user_id string, err error) {
	db := DB
	tx := DB.Begin()
	var newUser User
	newUser.User_name = user_name
	newUser.Password = user_password
	newUser.Score = 0
	dbret := tx.Create(&newUser)
	if dbret.Error != nil {
		tx.Rollback()
		return "", dbret.Error
	}
	tx.Commit()
	var lastUser  User
	dbret = db.Table("users").Last(&lastUser)
	if dbret.Error != nil {
		return "", dbret.Error
	}
	return lastUser.User_id, nil
}

// 修改用户 （密码、用户名）
func UpdateUser(user_id string, user_name string, user_password string) (error) {
	db := DB
	dbret := db.Model(&User{}).Where("user_id = ?", user_id).Updates(map[string]interface{}{"name": user_name, "password":user_password})
	if dbret.Error != nil {
		return dbret.Error
	}
	return nil
}

// 查询用户
func SearchUser(user_id string) (*User, error){
	db := DB
	var user User
	dbret := db.Where("user_id = ?", user_id).First(&user)
	if dbret.Error != nil {
		return nil, dbret.Error
	}
	return &user, nil
}

// 登录验证用户
func SearchUserForLogin(user_id string, user_password string) (bool, error){
	db := DB
	var user User
	dbret := db.Where("user_id = ?", user_id).First(&user)
	if dbret.Error != nil {
		return false, dbret.Error
	}
	if user.Password != user_password {
		return false, nil
	}
	return true, nil
}

// 查询用户垃圾统计信息
func GetWasteCount(user_id string) (int,map[int]int,error) {
	var bin_ids []int
	var waste_ids []int
	var types = map[int]int{-1:0, 0:0, 1:0, 2:0, 3:0}
	sum := 0
	db := DB
	dbret := db.Model(&UserBinRelation{}).Where("user_id = ?", user_id).Pluck("bin_id", &bin_ids)
	if dbret.Error != nil {
		return -1, types,dbret.Error
	}
	dbret = db.Model(&BinWasteRelation{}).Where("bin_id in (?)", bin_ids).Pluck("waste_id", &waste_ids)
	if dbret.Error != nil {
		return -1, types,dbret.Error
	}
	sum = len(waste_ids)
	var type_ids []int
	dbret = db.Model(&Waste{}).Where("id in (?)", waste_ids).Pluck("type_id", &type_ids)
	for i :=range type_ids {
		types[type_ids[i]] += 1
		sum++
	}
	return sum, types, nil
}

// 查询最近一周用户垃圾数据统计信息
func GetWeekWasteCount(user_id string) (int,map[int]int,error) {
	var bin_ids []int
	var waste_ids []int
	var types = map[int]int{-1:0, 0:0, 1:0, 2:0, 3:0}
	sum := 0
	db := DB
	dbret := db.Model(&UserBinRelation{}).Where("user_id = ?", user_id).Pluck("bin_id", bin_ids)
	if dbret.Error != nil {
		return -1, types,dbret.Error
	}
	dbret = db.Model(&BinWasteRelation{}).Where("bin_id in (?)", bin_ids).Pluck("waste_id", waste_ids)
	if dbret.Error != nil {
		return -1, types,dbret.Error
	}
	var now = int64(time.Now().Second())
	weekSecond := int64(7*24*60*60)
	weekTime := now - weekSecond
	sum = len(waste_ids)
	var type_ids []int
	dbret = db.Model(&Waste{}).Where("create_time > ? and  waste_id in (?)", weekTime,waste_ids).Pluck("type_id", type_ids)
	for i :=range type_ids {
		types[type_ids[i]] += 1
		sum++
	}
	return sum, types, nil
}

// 用户查询垃圾桶实时状态
func GetBinStatuses (user_id string) (map[string]int32 , error) {
	db := DB
	var res  = make(map[string]int32)
	var bin_ids []string
	dbret := db.Table("user_bin_relations").Where("user_id = ? ", user_id).Pluck("bin_id", &bin_ids)
	if dbret.Error != nil {
		fmt.Println(dbret.Error)
		return res, dbret.Error
	}
	for i, bin_id := range bin_ids {
		var bin Bin
		dbret := db.Table("bins").Where(" bin_id = ? ", bin_id).First(&bin)
		if dbret.Error != nil {
			return res, dbret.Error
		}
		res[bin_ids[i]] = int32(bin.Status)
	}
	return res, nil
}

// 用户查询垃圾桶实时信息
func GetBinsInfo (user_id string) ([]pb.BinInfoItem , int, error) {
	db := DB
	var bin_ids []string
	dbret := db.Table("user_bin_relations").Where("user_id = ? ", user_id).Pluck("bin_id", &bin_ids)
	if dbret.Error != nil {
		fmt.Println(dbret.Error)
		return nil, 0, dbret.Error
	}
	var res = make([]pb.BinInfoItem, len(bin_ids))
	for i, bin_id := range bin_ids {
		var bin Bin
		dbret := db.Table("bins").Where(" bin_id = ? ", bin_id).First(&bin)
		if dbret.Error != nil {
			return res, i, dbret.Error
		}
		res[i].Temp = bin.Temp
		res[i].Angel = bin.Angel
		res[i].IpAddress = bin.Ip_address
		res[i].BinId = int32(bin.Bin_id)
		res[i].Status = int32(bin.Status)
	}
	return res,len(bin_ids), nil
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

// 查询所有用户积分
func GetUserScores() (map[string]int32,error) {
	var users []User
	db := DB
	dbret := db.Find(&users)
	if dbret.Error != nil {
		funcName,file,line,_ := runtime.Caller(0)
		log.Println(  runtime.FuncForPC(funcName).Name(),file,line,dbret.Error.Error())
		return map[string]int32{}, dbret.Error
	}
	scores := make(map[string]int32, len(users))
	for _, user:= range users {
		scores[user.User_name] = int32(user.Score)
	}
	return scores, nil
}