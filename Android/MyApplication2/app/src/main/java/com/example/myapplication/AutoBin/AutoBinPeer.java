package com.example.myapplication.AutoBin;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Base64;
//import  org.apache.commons.codec.binary.Base64;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.common.AutoBinImageResponse;
import com.example.myapplication.common.AutoBinOprationType;
import com.example.myapplication.common.AutoBinStatusResponse;
import com.example.myapplication.common.CommonParameter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.myapplication.common.UserId;
import com.google.gson.Gson;

public class AutoBinPeer {

    public void startDetectState(AutoBinOprationType type,String ip,String data_id)  //开启心跳包检测数据更新状态
    {


        switch(type)
        {
            case Status:
                //开启心跳包，检测智能垃圾桶的参数和获取摄像机

                GetAutoBinStatusTask statusTask = new GetAutoBinStatusTask(ip, CommonParameter.route_binStatus, data_id);
                statusTask.execute();
                break;
            case Image:
                GetAutoBinImageTask imageTask = new GetAutoBinImageTask(ip, CommonParameter.route_binImage, data_id);
                imageTask.execute();
                break;
            case Motor:
                ControlMotorTask motorTask = new ControlMotorTask(ip, CommonParameter.route_binMotor, data_id);
                motorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;

        }
        //开启心跳包
//        Gson gson = new Gson();
//        UpdateRequest updateRequest = new UpdateRequest(MainActivity.Instance.username);
//        String json = gson.toJson(updateRequest);

//        Log.d("json", json);


    }
    class GetAutoBinStatusTask extends AsyncTask<Void, Void, Void> {

        String AutoBinIP;
        String rout_binStatus, data;

        GetAutoBinStatusTask( String ip_autoBin , String r_binStatus, String d) {
            AutoBinIP = ip_autoBin;
            rout_binStatus = r_binStatus;
            data = d;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while(MainActivity.Instance.MotorViewIsOn)
            {
                try {

                    getAutoBinStatus();

                    Thread.sleep(CommonParameter.duration_detect);
                    if(!MainActivity.Instance.MotorViewIsOn)
                    {
                        return null;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        void getAutoBinStatus()
        {
            try {
                Log.d("temp", "IP:" + CommonParameter.IP);
                URL url = new URL(CommonParameter.AutoBinIP + CommonParameter.testNeed +  rout_binStatus);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);         //是否输入参数
                connection.setDoInput(true);        //是否读取参数

                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_binStatus);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");               //设置请求属性
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(data);
                out.flush();
                out.close();


                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    //得到结果
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    char[] buffer = new char[1024];
                    int len = reader.read(buffer);
                    String dataResponse = new String(buffer, 0, len);
                    Gson gson = new Gson();
                    AutoBinStatusResponse response = gson.fromJson(dataResponse, AutoBinStatusResponse.class);
                    MainActivity.Instance.autoBinStatus = response;

                    Log.d("AutoBinStatusResponse", "获取成功:");

                    Message message = new Message();
                    message.what = 7;

                    MainActivity.Instance.handler.sendMessage(message);

                    //界面自动跳转
                    //TODO

                    reader.close();


                } else if (code != HttpURLConnection.HTTP_NOT_FOUND) {
                    //注册

//                    new MyRegisterTask(CommonParameter.route_register,data).execute();

//                    RegisterFun(CommonParameter.route_register, data);
                }
                Log.d("AutoBinStatusResp code", String.valueOf(code));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    class GetAutoBinImageTask extends AsyncTask<Void, Void, Void> {

        String AutoBinIP;
        String rout_binImage, data;

        GetAutoBinImageTask( String ip_autoBin , String r_binImage, String d) {
            AutoBinIP = ip_autoBin;
            rout_binImage = r_binImage;
            data = d;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while(MainActivity.Instance.CameraViewIsOn)
            {
                try {

                    getAutoBinImage();
                    Thread.sleep(CommonParameter.duration_detect);
                    if(!MainActivity.Instance.CameraViewIsOn)
                    {
                        return null;
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        void getAutoBinImage()
        {
            try {
                URL url = new URL(CommonParameter.AutoBinIP + CommonParameter.testNeed + rout_binImage);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);         //是否输入参数
                connection.setDoInput(true);        //是否读取参数

                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_binImage);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");               //设置请求属性
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(data);
                out.flush();
                out.close();

                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    //得到结果
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
//                    char[] buffer = new char[10000];
//                    int len = reader.read(buffer);
//                    String dataResponse = new String(buffer, 0, len);
                    Gson gson = new Gson();
//                    Log.d("Image", "Content:" + dataResponse);
                    AutoBinImageResponse response = gson.fromJson(reader,AutoBinImageResponse.class);
                    Log.d("Image", "length:" + response.image.length() + " " + response.image);
                    byte[] image = Base64.decode(response.image,Base64.DEFAULT);
//                    File file = new File("./t.jpg");
//                    FileOutputStream fileOutputStream = new FileOutputStream(file);
//                    fileOutputStream.write(image);
//                    fileOutputStream.close();
                    MainActivity.Instance.autoBinPhoto = BitmapFactory.decodeByteArray(image,0,image.length);

                    Log.d("AutoBinImageResponse", "获取成功:");

                    Message message = new Message();
                    message.what = 8;

                    MainActivity.Instance.handler.sendMessage(message);


                    reader.close();

                } else if (code != HttpURLConnection.HTTP_NOT_FOUND) {
                    //注册

//                    new MyRegisterTask(CommonParameter.route_register,data).execute();

//                    RegisterFun(CommonParameter.route_register, data);
                }
                Log.d("AutoBinImageResp code", String.valueOf(code));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    class ControlMotorTask extends AsyncTask<Void, Void, Void> {

        String AutoBinIP;
        String rout_Motor, request_motor;

        ControlMotorTask(String ip_autoBin, String r_motor, String d_controlMotor) {
            AutoBinIP = ip_autoBin;
            rout_Motor = r_motor;
            request_motor = d_controlMotor;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {


                URL url = new URL(CommonParameter.AutoBinIP  + CommonParameter.testNeed + rout_Motor + request_motor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(false);         //是否输入参数
                connection.setDoInput(true);        //是否读取参数

                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_binMotor);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");               //设置请求属性
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();


                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    //得到结果
                    Log.d("MotorResponse", "升降成功:");

                    Message message = new Message();
                    message.what = 9;

                    MainActivity.Instance.handler.sendMessage(message);

                    //界面自动跳转
                    //TODO


                } else if (code != HttpURLConnection.HTTP_NOT_FOUND) {
                    //注册

//                    new MyRegisterTask(CommonParameter.route_register,data).execute();

//                    RegisterFun(CommonParameter.route_register, data);
                }
                Log.d("MotorResponse code", String.valueOf(code));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
