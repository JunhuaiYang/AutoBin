package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.myapplication.AutoBin.AutoBinPeer;
import com.example.myapplication.AutoBin.Presenter;
import com.example.myapplication.activity.ui.LoginActivity;
import com.example.myapplication.activity.ui.SettingActivity;
import com.example.myapplication.common.AutoBinOprationType;
import com.example.myapplication.common.AutoBinStatusResponse;
import com.example.myapplication.common.AutoBinUserID;
import com.example.myapplication.common.BinInfoResponse;
import com.example.myapplication.common.BinState;
import com.example.myapplication.common.CommonParameter;
import com.example.myapplication.common.LoginRequest;
import com.example.myapplication.common.MotorRequest;
import com.example.myapplication.common.ServerOprationType;
import com.example.myapplication.common.RegisterRequest;
import com.example.myapplication.common.UpdateAccountRequest;
import com.example.myapplication.common.UserRanking;
import com.example.myapplication.server.ServerPeer;
import com.example.myapplication.ui.other.MyDialog;
import com.example.myapplication.ui.ranking.RankingFragment;
import com.example.myapplication.ui.state.StateFragment;
import com.example.myapplication.ui.statics.StaticsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements Presenter {

    public boolean isLogin = false, isRename = false, MotorViewIsOn = false , CameraViewIsOn = false;
    public String username, pwd, userid;
    public String username_rename, pwd_rename, userid_login, pwd_login, username_register, pwd_register;
    public int[] recycleTH, badTH, wetTH, dryTH;    //过去七天的各种垃圾的数量
    public int[] recycleTTH, badTTH, wetTTH, dryTTH, unidenTTH, totalTTH;    //今日的各种垃圾数量和历史总垃圾数量

    public BinState[] binStates;    //各个垃圾桶的状态
    public UserRanking[] userRankings;  //所有用户的排行榜

    public BinInfoResponse binInfoResponse; //所有垃圾桶的详细信息；

    public AutoBinStatusResponse autoBinStatus = null;  //垃圾桶状态信息
    public Bitmap  autoBinPhoto = null;     //垃圾桶拍照信息

    public MyDialog handMotorDialog = null;
    public MyDialog photoDialog = null;



    public static MainActivity Instance;
    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;

    NavigationView navigationView;
    NavController navController;

    public Handler handler;




    public SharedPreferences userInfoStorage, staticsInfoStorage;

    Button headBtn, settingBtn;
    TextView tvUsername, tvUserid;


    private AppBarConfiguration mAppBarConfiguration;

    ServerPeer serverPeer;
    AutoBinPeer autoBinPeer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_ranking, R.id.nav_statics, R.id.nav_state,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        serverPeer = new ServerPeer();
        autoBinPeer = new AutoBinPeer();

        //数据初始化
        initData();


        setStorage();
        setHeader();

        setHandler();

        //进行默认先登陆



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                UpdateView();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initData()
    {
        recycleTH = new int[]{1, 2, 3, 4, 5, 6, 7};
        badTH = new int[]{2, 3, 4, 5, 6, 7, 8};
        wetTH = new int[]{3, 4, 5, 6, 7, 8, 9};
        dryTH = new int[]{4, 5, 6, 7, 8, 9, 10};

//        recycleTH = new int[7];
//        badTH = new int[7];
//        wetTH = new int[7];
//        dryTH = new int[7];

        recycleTTH = new int[]{0,1,2};    //0:今日所扔垃圾，1:一周内所扔垃圾， 2:历史所扔垃圾
        badTTH = new int[3];
        wetTTH = new int[3];
        dryTTH = new int[3];
        totalTTH = new int[3];
        unidenTTH = new int[3];

        binStates = new BinState[1];
        BinState binState = new BinState("未连接任意垃圾桶",1);
        binStates[0] = binState;

        userRankings = new UserRanking[1];
        UserRanking userRanking = new UserRanking(1,"未登录", 0);
        userRankings[0] = userRanking;


        binInfoResponse = new BinInfoResponse(1,1,1,1,1,"127.0.0.1");
    }

    //基本内部存储设计
    private void setStorage()
    {
        userInfoStorage =getSharedPreferences("userdata", MODE_PRIVATE);
        staticsInfoStorage = getSharedPreferences("statics", MODE_PRIVATE);

    }
    //头部设置
    private void setHeader()
    {
        headBtn = navigationView.getHeaderView(0).findViewById(R.id.btn_head);
        settingBtn = navigationView.getHeaderView(0).findViewById(R.id.btn_setting);
        tvUsername = navigationView.getHeaderView(0).findViewById(R.id.tv_username);
        tvUserid = navigationView.getHeaderView(0).findViewById(R.id.tv_userid);

        headBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setHandler()
    {
        handler = new Handler()
        {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 1:
                        LoginSuccess();
                        break;
                    case 2:
                        RenameSuccess();
                        break;
                    case 3:
                        UpdateView();    //更新界面
                        break;
                    case 4:
                        RegisterSuccess(msg.getData().getString("user_id"));
                        break;
                    case 5:
                        QuerySuccess(msg.getData().getString("user_name"));
                        break;
                    case 6:
                        UpdateViewSuccess();
                        break;
                    case 7:
                        UpdateMotorView();
                        break;
                    case 8:
                        UpdateCameraView();
                        break;
                    case 9:
                        break;
                }

            }
        };
    }
    public void Login(String login_userid, String login_userpwd)
    {
        if(login_userid.length()<1 || login_userpwd .length()<5  )    //密码验证
        {
            Toast.makeText(MainActivity.this, "用户名或密码长度错误", Toast.LENGTH_SHORT).show();
        }


        //记录登陆信息
        userid_login = login_userid;
        pwd_login = login_userpwd;


        //转换为json数据传输
        Gson gson = new Gson();
        LoginRequest loginRequest = new LoginRequest(userid_login,pwd_login);
        String json = gson.toJson(loginRequest);

        Log.d("json" , json);

        serverPeer.sendMessage(ServerOprationType.Login, CommonParameter.route_login, json);
    }
    public void LoginSuccess()        //登陆成功
    {
        userid = userid_login;
        pwd = pwd_login;
        //存储
        SharedPreferences.Editor editor = MainActivity.Instance.userInfoStorage.edit();
        editor.putString("user_id", userid);
        editor.putString("pwd", pwd);
        editor.apply();
        Toast.makeText(MainActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
        isLogin = true;     //登陆状态为true

        //将进入界面的头像和名称改为用户名
        tvUserid.setText(userid);

        //获取用户名
        QueryUsername(userid);


        //开启更新心跳包
//        serverPeer.startDetectState();

        //更新界面
        UpdateView();

    }


    public void Register(String username, String pwd)   //注册功能
    {
        //判断是否符合要求
        if((username.length()<5) || (pwd.length()<5 ))
        {
            Toast.makeText(MainActivity.this,"长度不足", Toast.LENGTH_SHORT).show();
            return;

        }
        username_register = username;
        pwd_register = pwd;
        RegisterRequest registerRequest = new RegisterRequest(username, pwd);
        Gson gson = new Gson();
        String json = gson.toJson(registerRequest);

        serverPeer.sendMessage(ServerOprationType.Register, CommonParameter.route_user, json);
    }
    public void RegisterSuccess(String id)   //注册成功
    {

        username = username_register;
        pwd = pwd_register;
        userid = id;
        SharedPreferences.Editor editor = userInfoStorage.edit();
        editor.putString("user_id", userid);
        editor.putString("pwd", pwd);
        editor.putString("user_name", username);
        editor.apply();

        Toast.makeText(MainActivity.this,"注册成功",Toast.LENGTH_SHORT).show();

        //设置界面
        tvUsername.setText(username);
        Log.d("temp", "username:" + username);
        tvUserid.setText(userid);

        if(LoginActivity.Instance != null)
        {
            LoginActivity.Instance.setDefault(userid, pwd);
        }


        //自动登陆
        //TODO



    }

    public void QueryUsername(String query_id)
    {
        if(query_id.length()<1)
        {
            return;
        }
//        UserId userId = new UserId(query_id);
//        Gson gson = new Gson();
//        String json = gson.toJson(userId, UserId.class);
        String data = "?user_id=" + query_id;
        serverPeer.sendMessage(ServerOprationType.Query, CommonParameter.route_user, data);

    }
    public void QuerySuccess(String query_username)
    {
        tvUsername.setText(query_username);
    }


    public void Logoff()
    {

        //停止心跳包
        isLogin = false;

        //更改界面
        tvUsername.setText("未登陆");
        tvUserid.setText("userid");

        Toast.makeText(MainActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
    }

    public void Rename(String newUsername, String newPwd)
    {
        //判断是否符合要求
        if((newUsername.length() != 0 && newUsername.length() < 5) || (newPwd.length() != 0 && newPwd.length()<5 ))
        {
            Toast.makeText(MainActivity.this,"长度不足", Toast.LENGTH_SHORT).show();
            return;

        }

        if(newUsername.length() == 0)
        {
            newUsername = username;
        }
        if(newPwd.length() == 0)
        {
            newPwd = pwd;
        }
        //暂存用户名和密码
        username_rename = newUsername;
        pwd_rename = newPwd;

        //转换成json
        UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest(newUsername,userid,newPwd);
        Gson gson = new Gson();
        String json = gson.toJson(updateAccountRequest);


        serverPeer.sendMessage(ServerOprationType.Rename, CommonParameter.route_user, json);    //发送更新请求
    }
    public void RenameSuccess()
    {
        //存储新的信息
        SharedPreferences.Editor editor = userInfoStorage.edit();
        editor.putString("user_name", username_rename);
        editor.putString("pwd", pwd_rename);
        editor.apply();
        username = username_rename;
        pwd = pwd_rename;


        //修改界面
        tvUsername.setText(username);

        //提示
        Toast.makeText(MainActivity.this,"修改成功",Toast.LENGTH_SHORT).show();

    }

    public void UpdateView()
    {
        serverPeer.sendMessage(ServerOprationType.Update,"", "?" + "user_id=" + userid);
    }

    public void UpdateViewSuccess()
    {
        if(StaticsFragment._instance != null)
        {
            StaticsFragment._instance.updateView();
        }
        if(RankingFragment._instance != null)
        {
            RankingFragment._instance.updateView();
        }
        if(StateFragment._instance != null)
        {
            StateFragment._instance.updateView();
        }
        Toast.makeText(MainActivity.this, "更新成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void GetAutoBinInfo(String autoBinIp) {

        AutoBinUserID userID = new AutoBinUserID(Integer.parseInt(userid));
        Gson gson = new Gson();
        String json = gson.toJson(userID, AutoBinUserID.class);

        autoBinPeer.startDetectState(AutoBinOprationType.Status, autoBinIp, json);

    }

    @Override
    public void UpdateMotorView() {
        if (handMotorDialog != null) {
            handMotorDialog.Update();
        }

    }

    @Override
    public void GetCamera(String autoBinIp) {
        if(userid == null) return;
        AutoBinUserID userID = new AutoBinUserID(Integer.valueOf(userid));
        Gson gson = new Gson();
        String json = gson.toJson(userID, AutoBinUserID.class);

        autoBinPeer.startDetectState(AutoBinOprationType.Image, autoBinIp, json);

    }

    @Override
    public void UpdateCameraView() {
        if(photoDialog != null)
        {
            photoDialog.Update();
        }

    }

    @Override
    public void ControlMotor(String ip, int motor_numb, int dirc) //0是降，1是升
    {
        autoBinPeer.startDetectState(AutoBinOprationType.Motor, ip, "?user_id=" + userid + "&motor=" + motor_numb +"&dirc=" + dirc);
    }


}
