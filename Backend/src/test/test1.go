package main

import "fmt"

func main(){
	arr := make([]int,2)
	arr1 := append(arr, 1, 2, 3, 4, 5) //将3,4,5追加到arr中
	for elem := range  arr1 {
		fmt.Print(elem)
	}
	fmt.Println()
	for elem := range  arr {
		fmt.Print(elem)
	}
	fmt.Println()

	arr2 := []int{1,2,3,4,5,6,7,8}
	copy(arr1,arr2)
	for elem := range  arr {
		fmt.Print(elem)
	}
}

