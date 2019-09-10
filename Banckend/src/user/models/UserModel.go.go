package models

type userLogin struct {
	user_id 		string
	user_password	string
}



type User struct {
	user_id 		string	`gorm:"column:user_id;primary_key;AUTO_INCREMENT"`
	user_name		string	`gorm:"column:name;primary_key;"`
	password		string 	`gorm:"column:password;"`
	score			int		`gorm:"column:score;"`
}