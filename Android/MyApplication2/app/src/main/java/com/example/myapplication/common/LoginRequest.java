package com.example.myapplication.common;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginRequest {
    String user_id;
    String user_password;

//    ArrayList<String> author = new ArrayList<String>()
//    {
//        {
//            add(user);
//            add(password);
//        }
//    };

//    public HashMap<String, String> map = new HashMap<String,String>();

    public LoginRequest(String u, String p)
    {
        user_id = u;
        user_password = p;
    }



}
