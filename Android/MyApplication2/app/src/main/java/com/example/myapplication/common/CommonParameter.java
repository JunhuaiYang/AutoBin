package com.example.myapplication.common;

public class CommonParameter {
    public static String IP = "http://192.168.1.199:8081/";
    public static String route_login = "autobin/user/login";
    public static String route_user = "autobin/user";
    public static String route_state = "autobin/user/bin_status";
    public static String route_waste = "autobin/user/waste";
    public static String route_history = "autobin/user/week_waste";
    public static String route_ranking = "autobin/user/scores";

    //test
//    public static String IP = "http://192.168.1.108:8080/temp_war_exploded/";
//    public static String route_login = "login";    //test
//    public static String route_state = "state";
//    public static String route_user = "user";
//    public static String route_waste = "update";
//    public static String route_history = "history";
//    public static String route_ranking = "ranking";

    public static String method_register = "POST";
    public static String method_login = "POST";
    public static String method_rename = "PUT";
    public static String method_state = "GET";
    public static String method_query = "GET";
    public static String method_waste = "GET";
    public static String method_weekwaste = "GET";
    public static String method_ranking = "GET";

    public static int duration_detect = 3000;


}

