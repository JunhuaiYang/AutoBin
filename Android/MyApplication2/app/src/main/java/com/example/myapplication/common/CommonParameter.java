package com.example.myapplication.common;

public class CommonParameter {
//    public static String IP = "http://192.168.1.199:8081/";
    public static String IP = "http://192.168.43.43:8081/";
//    public static String AutoBinIP = "http://192.168.1.199:8082/";
    public static String AutoBinIP = "http://192.168.43.43:8082/";

    public static String route_login = "autobin/user/login";
    public static String route_user = "autobin/user";
    public static String route_state = "autobin/user/bin_status";
    public static String route_waste = "autobin/user/waste";
    public static String route_history = "autobin/user/week_waste";
    public static String route_ranking = "autobin/user/scores";
    public static String route_stateInfo = "autobin/user/bin_info";

//    与AutoBin交互
    public static String AutoBinPort = "8082";
    public static String testNeed = "";
    public static String route_binStatus = "autobin/status";
    public static String route_binMotor = "autobin/binmotor";
    public static String route_binImage = "autobin/binimage";

    //test
//    public static String IP = "http://192.168.1.108:8080/temp_war_exploded/";
//
//    public static String route_login = "login";    //test
//    public static String route_state = "state";
//    public static String route_user = "user";
//    public static String route_waste = "update";
//    public static String route_history = "history";
//    public static String route_ranking = "ranking";
//
//    public static String route_stateInfo = "stateinfo";
//
//
//    public static String AutoBinPort = "8080";
//    public static String testNeed = "temp_war_exploded/";
//    public static String route_binStatus = "binstatus";
//    public static String route_binMotor = "binmotor";
//    public static String route_binImage = "binimage";

    public static String method_register = "POST";
    public static String method_login = "POST";
    public static String method_rename = "PUT";
    public static String method_query = "GET";
    public static String method_ranking = "GET";
    public static String method_state = "GET";
    public static String method_waste = "GET";
    public static String method_weekwaste = "GET";
    public static String method_stateInfo = "GET";
    public static String method_binMotor = "GET";
    public static String method_binImage = "POST";
    public static String method_binStatus = "POST";


    public static int duration_detect = 1000;


    public static int pos_recycle = 0;
    public static int pos_bad = 1;
    public static int pos_wet = 2;
    public static int pos_dry = 3;
    public static int pos_unId = 4;

    //智能垃圾桶状态
    public static final int status_unId = -2; //无法识别垃圾，需要取出
    public static final int status_unCon = -1;//连接失败
    public static final int status_normal = 1;//正常
    public static final int status_isHandling = 2;    //正在处理垃圾
    public static final int status_angleTrouble = 3;  //平板角度有问题，需要处理

//    public static enum MessageFunc{LoginSuc, RenameSuc, UpdateView, RegisterSuc, QuerySuc, UpdateViewSuc, UpdateMotorViewSuc, UpdateCameraViewSuc};

}

