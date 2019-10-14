package com.example.myapplication.common;

public class UpdateAccountRequest {
    public String user_name;
    public String user_id;
    public String user_password;

//    ArrayList<String> author = new ArrayList<String>()
//    {
//        {
//            add(user);
//            add(password);
//        }
//    };

//    public HashMap<String, String> map = new HashMap<String,String>();

    public UpdateAccountRequest(String ou, String u, String p)
    {
        user_name = ou;
        user_id = u;
        user_password = p;
    }



}
