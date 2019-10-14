package com.example.myapplication.activity.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class SettingActivity extends AppCompatActivity {

    Button renameBtn, logoffBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        renameBtn = findViewById(R.id.btn_rename);
        logoffBtn = findViewById(R.id.btn_logoff);

        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.Instance.isLogin == false)  //未登录
                {
                    Toast.makeText(SettingActivity.this, "请先登录后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder4 = new AlertDialog.Builder(SettingActivity.this);
                View view1 = LayoutInflater.from(SettingActivity.this).inflate(R.layout.layout_dialog_rename, null);
                final EditText newUsernameTv = view1.findViewById(R.id.tv_new_username);
                final EditText newPwdTv = view1.findViewById(R.id.tv_new_password);
                Button renameBtn = view1.findViewById(R.id.btn_rename);
                renameBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //点击后推出
                        //通过重写Listenner,添加参数builder,
                        //builder.dismiss();

                        String username, pwd;
                        username = newUsernameTv.getText().toString();
                        pwd = newPwdTv.getText().toString();

                        MainActivity.Instance.Rename(username,pwd);

                    }
                });
                builder4.setTitle("请输入新的用户名和密码(为空即不修改)").setView(view1)
                        .show();

            }
        });
        logoffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.Instance.isLogin == false)  //未登录
                {
                    Toast.makeText(SettingActivity.this, "请先登录后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                AlertDialog alertDialog = builder.setTitle("注销")
                        .setMessage("确定注销吗？")
                        .setPositiveButton("确定", new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.Instance.Logoff();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });
    }
}
