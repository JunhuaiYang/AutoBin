package com.example.myapplication.ui.other;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.common.CommonParameter;

import java.text.DecimalFormat;

public class MyDialog {
    public Context mContext;
    public TextView tv_status, tv_angle, tv_temp;  //用在motor界面种

    public ImageView image_photo;

    public MyDialog(Context context, TextView id, TextView a, TextView t)
    {
        mContext = context;
        tv_status = id;
        tv_angle = a;
        tv_temp = t;
    }
    public MyDialog(ImageView iv)
    {
        image_photo = iv;
    }
    public void Update()
    {
        if(tv_status != null)
        {
            switch (MainActivity.Instance.autoBinStatus.status)
            {
                case CommonParameter.status_unId:
                    tv_status.setText("垃圾识别失败");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                    break;
                case CommonParameter.status_unCon:
                    tv_status.setText("连接失败");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                    break;
                case CommonParameter.status_normal:
                    tv_status.setText("正常");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                    break;
                case CommonParameter.status_isHandling:
                    tv_status.setText("正在处理垃圾");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                    break;
                case CommonParameter.status_angleTrouble:
                    tv_status.setText("平板角度有问题");
                    tv_status.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                    break;
            }

        }
        if(tv_angle != null)
        {
            DecimalFormat decimalFormat=new DecimalFormat(".00");
            String num = decimalFormat.format(MainActivity.Instance.autoBinStatus.angel);
            tv_angle.setText(num + "°");
        }
        if(tv_temp != null)
        {

            DecimalFormat decimalFormat=new DecimalFormat(".00");
            String num = decimalFormat.format(MainActivity.Instance.autoBinStatus.temp);
            tv_temp.setText(num + "°C");
        }
        if(image_photo != null)
        {
            image_photo.setImageBitmap(MainActivity.Instance.autoBinPhoto);
        }
    }
}
