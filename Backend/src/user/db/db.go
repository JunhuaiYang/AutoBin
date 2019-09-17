package db

type User struct {
	user_id 		string	`gorm:"column:user_id;primary_key;AUTO_INCREMENT"`
	user_name		string	`gorm:"column:name;primary_key;"`
	password		string 	`gorm:"column:password;"`
	score			int		`gorm:"column:score;"`
}

// 添加用户
func AddUer(newUser *User) (error) {
	db := DB
	dbret := db.Create(&newUser)
	if dbret.Error != nil {
		return dbret.Error
	}
	return nil
}

// 修改用户
func UpdateUer(newUser *User) (error) {
	db := DB
	dbret := db.Create(&newUser)
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