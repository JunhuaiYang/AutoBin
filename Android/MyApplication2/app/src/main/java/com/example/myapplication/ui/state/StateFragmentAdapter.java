package com.example.myapplication.ui.state;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AutoBin.AutoBinPeer;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.common.CommonParameter;
import com.example.myapplication.ui.other.MyDialog;

public class StateFragmentAdapter extends RecyclerView.Adapter<StateFragmentAdapter.ViewHolder> {

    private Context mContext;


    public StateFragmentAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public StateFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StateFragmentAdapter.ViewHolder holder = new StateFragmentAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_state_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StateFragmentAdapter.ViewHolder holder, final int position) {
        boolean flag_motor = true;

        if(MainActivity.Instance.binStates == null || MainActivity.Instance.binStates.length <= 0)
        {
            return;
        }
        holder.tv_name.setText(MainActivity.Instance.binStates[position].BinName);
        switch(MainActivity.Instance.binStates[position].BinStatus) {
            case CommonParameter.status_normal:
                holder.tv_state.setText("正常");
                holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                holder.btn_state.setTextColor(mContext.getResources().getColor(R.color.colorGreen));

                break;

            case CommonParameter.status_unId:
                holder.tv_state.setText("识别失败");
                holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                holder.btn_state.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                break;

            case CommonParameter.status_unCon:
                holder.tv_state.setText("连接失败");
                holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                holder.btn_state.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                flag_motor = false;
                break;

            case CommonParameter.status_isHandling:
                holder.tv_state.setText("正在处理垃圾");
                holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                holder.btn_state.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                break;

            case CommonParameter.status_angleTrouble:
                holder.tv_state.setText("平板角度问题");
                holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                holder.btn_state.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                break;

        }
        if(flag_motor)
        {
            holder.btn_state.setEnabled(true);
            Log.d("temp", "position:" + position);
            holder.btn_state.setOnClickListener(new View.OnClickListener() {    //设置摄像头按钮点击后的反应


                String binIP = MainActivity.Instance.binInfoResponse.bin_info[position].ip_address;
                ImageView iv_photo;
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    View view1 = LayoutInflater.from(mContext).inflate(R.layout.layout_photo, null);
                    iv_photo = view1.findViewById(R.id.image);
                    MyDialog myDialog = new MyDialog(iv_photo);
                    MainActivity.Instance.photoDialog = myDialog;

                    //开启心跳包检测照片
                    MainActivity.Instance.CameraViewIsOn = true;
                    MainActivity.Instance.GetCamera(binIP);

                    AlertDialog alertDialog = builder.setTitle("实时图像").setView(view1)
                            .show();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {  //关闭对话框时关闭心跳包
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            MainActivity.Instance.CameraViewIsOn = false;
                            MainActivity.Instance.photoDialog = null;
                        }
                    });
                }
            });
            holder.tv_state.setOnClickListener(new View.OnClickListener() {  //设置垃圾桶状态钮按点击后的反应
                String binIP = MainActivity.Instance.binInfoResponse.bin_info[position].ip_address;
                TextView tv_status, tv_angle, tv_temp;
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    View view1 = LayoutInflater.from(mContext).inflate(R.layout.layout_bin_control, null);
                    tv_status = view1.findViewById(R.id.tv_status);
                    tv_angle = view1.findViewById(R.id.tv_angle);
                    tv_temp = view1.findViewById(R.id.tv_temp);
                    MyDialog myDialog = new MyDialog(mContext,tv_status, tv_angle, tv_temp);
                    MainActivity.Instance.handMotorDialog = myDialog;

                    //开启心跳包检测状态
                    MainActivity.Instance.MotorViewIsOn = true;
                    MainActivity.Instance.GetAutoBinInfo(binIP);
                    Button btn1_add = view1.findViewById(R.id.btn1_add);
                    Button btn1_sub = view1.findViewById(R.id.btn1_sub);
                    Button btn2_add = view1.findViewById(R.id.btn2_add);
                    Button btn2_sub = view1.findViewById(R.id.btn2_sub);
                    Button btn3_add = view1.findViewById(R.id.btn3_add);
                    Button btn3_sub = view1.findViewById(R.id.btn3_sub);
                    Button btn4_add = view1.findViewById(R.id.btn4_add);
                    Button btn4_sub = view1.findViewById(R.id.btn4_sub);
                    AlertDialog alertDialog = builder.setTitle("手动模式").setView(view1)
                            .show();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {  //关闭对话框时关闭心跳包
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            MainActivity.Instance.MotorViewIsOn = false;
                            MainActivity.Instance.handMotorDialog = null;
                        }
                    });
                    btn1_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {    //发消息
                            MainActivity.Instance.ControlMotor(binIP, 1, 1);
                        }
                    });
                    btn1_sub.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {    //发消息
                            MainActivity.Instance.ControlMotor(binIP, 1, 0);
                        }
                    });
                    btn2_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {    //发消息
                            MainActivity.Instance.ControlMotor(binIP, 2, 1);
                        }
                    });
                    btn2_sub.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {    //发消息
                            MainActivity.Instance.ControlMotor(binIP, 2, 0);
                        }
                    });
                    btn3_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {    //发消息
                            MainActivity.Instance.ControlMotor(binIP, 3, 1);
                        }
                    });
                    btn3_sub.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {    //发消息
                            MainActivity.Instance.ControlMotor(binIP, 3, 0);
                        }
                    });
                    btn4_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {    //发消息
                            MainActivity.Instance.ControlMotor(binIP, 4, 1);
                        }
                    });
                    btn4_sub.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {    //发消息
                            MainActivity.Instance.ControlMotor(binIP, 4, 0);
                        }
                    });
                }


            });



        }
        else
        {
            holder.btn_state.setEnabled(false);
        }
    }


    @Override
    public int getItemCount() {
        if(MainActivity.Instance.binStates != null)
        {
            return MainActivity.Instance.binStates.length;
        }
        else
        {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_state;
        Button btn_state;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_state = itemView.findViewById(R.id.tv_state);
            btn_state = itemView.findViewById(R.id.btn_state);
        }
    }
}
