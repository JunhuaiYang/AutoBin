package com.example.myapplication.AutoBin;

public interface Presenter {
    public void Login(String login_userid, String login_userpwd);
    public void LoginSuccess();        //登陆成功
    public void Register(String username, String pwd);   //注册功能
    public void RegisterSuccess(String id);   //注册成功
    public void QueryUsername(String query_id);
    public void QuerySuccess(String query_username);
    public void Logoff();
    public void Rename(String newUsername, String newPwd);
    public void RenameSuccess();
    public void UpdateView();
    public void UpdateViewSuccess();
    public void GetAutoBinInfo(String autoBinIp);
    public void UpdateMotorView();
    public void GetCamera(String autoBinIp);
    public void UpdateCameraView();
    public void ControlMotor(String ip, int motor_numb, int dirc);

}
