package com.example.myapplication.activity.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    public static LoginActivity Instance;
    EditText tv_userid, tv_pwd;
    Button btn_login, btn_register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Instance = this;




        tv_userid = findViewById(R.id.tv_userid);
        tv_pwd = findViewById(R.id.tv_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);


        btn_login.setEnabled(true);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.Instance.Login(tv_userid.getText().toString(), tv_pwd.getText().toString());
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder4 = new AlertDialog.Builder(LoginActivity.this);
                View view1 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_register, null);
                final EditText newUsernameTv = view1.findViewById(R.id.tv_username);
                final EditText newPwdTv = view1.findViewById(R.id.tv_password);
                Button registerBtn = view1.findViewById(R.id.btn_register);
                registerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //点击后推出
                        //通过重写Listenner,添加参数builder,
                        //builder.dismiss();

                        String username, pwd;
                        username = newUsernameTv.getText().toString();
                        pwd = newPwdTv.getText().toString();

                        MainActivity.Instance.Register(username,pwd);

                    }
                });
                builder4.setTitle("注册").setView(view1)
                        .show();
            }
        });

        //恢复曾经存储的登陆信息
        String tmpUserid, tmpPwd;
        tmpUserid = MainActivity.Instance.userInfoStorage.getString("user_id","");
        tmpPwd = MainActivity.Instance.userInfoStorage.getString("pwd", "");

        tv_userid.setText(tmpUserid);
        tv_pwd.setText(tmpPwd);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Instance = null;
    }

    public void setDefault(String user_id, String user_pwd)
    {
        tv_userid.setText(user_id);
        tv_pwd.setText(user_pwd);
    }
}
