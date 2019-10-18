package main
import (
	pb "../user/protos"
	"fmt"
)


// GetMap 得到map及其所有的key
// keys : map中所有的key，已排序，从小到大
func GetMap() (result map[string]int32, value []int32) {
	result = map[string]int32{}
	value = []int32{}
	// 压入各个数据
	result["A"] = 223
	result["B"] = 91
	result["C"] = 13
	result["D"] = 330
	result["E"] = 100

	// 得到各个key
	res := make([]pb.UserScore,len(result))
	i := 0
	for key, val := range result {
		res[i].Score = val
		res[i].UserName = key
		res[i].Ranking = int32(i+1)
		i++
	}
	for i :=0; i < len(res); i++ {
		for j :=len(res)-1; j > i; j-- {
			if res[j].Score > res[j-1].Score {
				temp := res[j].Score
				res[j].Score = res[j-1].Score
				res[j-1].Score = temp
				temp1 := res[j].UserName
				res[j].UserName =  res[j-1].UserName
				res[j-1].UserName = temp1
			}
		}
	}
	for i :=0; i < len(res); i++ {
		fmt.Println(res[i].Ranking, res[i].UserName, res[i].Score)
	}
	return
}

func main() {
	GetMap()
}