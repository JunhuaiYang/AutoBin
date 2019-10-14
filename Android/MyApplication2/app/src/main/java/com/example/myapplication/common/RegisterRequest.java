package com.example.myapplication.common;

public class RegisterRequest {
    public String user_name;
    public String user_password;

    public RegisterRequest(String name, String pwd)
    {
        user_name = name;
        user_password = pwd;
    }
}
