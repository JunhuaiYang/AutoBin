package com.example.myapplication.server;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.common.BinState;
import com.example.myapplication.common.BinStateResponse;
import com.example.myapplication.common.CommonParameter;
import com.example.myapplication.common.MessageType;
import com.example.myapplication.common.QueryResponse;
import com.example.myapplication.common.RankingResponse;
import com.example.myapplication.common.UserId;
import com.example.myapplication.common.UserRanking;
import com.example.myapplication.common.WasteResponse;
import com.example.myapplication.common.WeekWasteResponse;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServerPeer {
    public static ServerPeer Instance;  //单例模式


    private String IP = CommonParameter.IP;

    public ServerPeer() {
        Instance = this;


    }

    public void startDetectState()  //开启心跳包检测数据更新状态
    {

        //开启心跳包
//        Gson gson = new Gson();
//        UpdateRequest updateRequest = new UpdateRequest(MainActivity.Instance.username);
//        String json = gson.toJson(updateRequest);

//        Log.d("json", json);

        sendMessage(MessageType.Update, CommonParameter.route_waste, "?" + "user_id=" + MainActivity.Instance.userid);

        ;
    }

    public void sendMessage(MessageType type, String route, String data) {
        switch (type) {
            case Login:


                MyLoginTask myLoginTask = new MyLoginTask(route, data);

                myLoginTask.execute();
                break;
            case Register:
                MyRegisterTask myRegisterTask = new MyRegisterTask(route, data);
                myRegisterTask.execute();
                break;
            case Rename:
                MyRenameTask myRenameTask = new MyRenameTask(route, data);
                myRenameTask.execute();
                break;
            case Update:
                MyUpdateTask myUpdateTask = new MyUpdateTask(route, data);
//                myUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                myUpdateTask.execute();
                break;
            case Query:
                MyQueryTask myQueryTask = new MyQueryTask(route, data);
                myQueryTask.execute();
                break;
        }


    }

    //修改用户名和密码任务
    class MyRenameTask extends AsyncTask<Void, Void, Void> {

        String route, data;

        MyRenameTask(String r, String d) {
            route = r;
            data = d;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {


                URL url = new URL(IP + route);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);         //是否输入参数
                connection.setDoInput(true);        //是否读取参数

                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_rename);
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
                    Log.d("RenameResponse", "修改成功:");

                    Message message = new Message();
                    message.what = 2;

                    MainActivity.Instance.handler.sendMessage(message);

                    //界面自动跳转
                    //TODO


                } else if (code != HttpURLConnection.HTTP_NOT_FOUND) {
                    //注册

//                    new MyRegisterTask(CommonParameter.route_register,data).execute();

//                    RegisterFun(CommonParameter.route_register, data);
                }
                Log.d("loginResponse code", String.valueOf(code));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    //登陆任务
    class MyLoginTask extends AsyncTask<Void, Void, Void> {

        String route, data;

        MyLoginTask(String r, String d) {
            route = r;
            data = d;

        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(IP + route);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);         //是否输入参数
                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_login);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");               //设置请求属性
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

//            String json = java.net.URLEncoder.encode(obj.toString(), "utf-8");
//            out.write("data".getBytes());

                out.writeBytes(data);
                out.flush();
                out.close();

                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    //登陆成功

                    Message message = new Message();
                    message.what = 1;
                    MainActivity.Instance.handler.sendMessage(message);

                    //界面自动跳转
                    //TODO


                }
                Log.d("loginResponse code", String.valueOf(code));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    //注册任务
    class MyRegisterTask extends AsyncTask<Void, Void, Void> {

        String route, data;

        public MyRegisterTask(String r, String d) {
            route = r;
            data = d;

        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {


                URL url = new URL(IP + route);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);         //是否输入参数
                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_register);
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
                    UserId userId = gson.fromJson(dataResponse, UserId.class);

                    Log.d("registerResponse", "content:" + dataResponse);

                    //注册成功
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", userId.user_id);
                    message.setData(bundle);
                    message.what = 4;
                    MainActivity.Instance.handler.sendMessage(message);

                    //界面自动跳转
                    //TODO

                }
                Log.d("registerResponse code", String.valueOf(code));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    //查询用户名任务
    class MyQueryTask extends AsyncTask<Void, Void, Void> {


        String route, data;

        public MyQueryTask(String r, String d) {
            route = r;
            data = d;

        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {


                URL url = new URL(IP + route + data);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(false);         //是否输入参数,get方法无法使用output
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_query);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");               //设置请求属性
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
//                connection.connect();
                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    //得到结果
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    char[] buffer = new char[1024];
                    int len = reader.read(buffer);

                    reader.close();
                    String dataResponse = new String(buffer, 0, len);
                    Gson gson = new Gson();
                    QueryResponse queryResponse = gson.fromJson(dataResponse, QueryResponse.class);

                    Log.d("queryResponse", "content:" + dataResponse);

                    //注册成功
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", queryResponse.user_id);
                    bundle.putString("user_name", queryResponse.user_name);
                    bundle.putString("user_password", queryResponse.user_password);
                    bundle.putString("user_score", String.valueOf(queryResponse.user_score));
                    message.setData(bundle);
                    message.what = 5;
                    MainActivity.Instance.handler.sendMessage(message);

                    //界面自动跳转
                    //TODO

                }
                Log.d("queryResponse code", String.valueOf(code));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //检测更新任务
    class MyUpdateTask extends AsyncTask<Void, Void, Void> {

        String route, data;


        public MyUpdateTask(String r, String d) {
            route = r;
            data = d;

        }


        @Override
        protected Void doInBackground(Void... voids) {


//            while (true)    //注销后退出
//            {

                if (!MainActivity.Instance.isLogin) {
                    return null;
                }
//                try {
//                    Thread.sleep(CommonParameter.duration_detect);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (!MainActivity.Instance.isLogin) {
//                    return null;
//                }
                updateWaste();
                updateWeekWaste();
                updateRanking();
                updateBinStates();

                //更新界面

                Message message = new Message();
                message.what = 6;
                MainActivity.Instance.handler.sendMessage(message);
//            }
            return null;
        }

        private void updateWaste()
        {
            try {

                URL url = new URL(IP + CommonParameter.route_waste + data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(false);         //是否输入参数
                connection.setDoInput(true);        //是否读取参数
                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_waste);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");               //设置请求属性
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    //得到结果
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    char[] buffer = new char[1024];
                    int len = reader.read(buffer);

                    reader.close();
                    String readData = new String(buffer, 0, len);
                    Log.d("WasteResponse", "content:" + readData);
                    Gson gson = new Gson();
                    WasteResponse wasteResponse;

                    //将得到的更新回应转为数据
                    wasteResponse = gson.fromJson(readData, WasteResponse.class);
                    //更新数据和界面
                    MainActivity.Instance.dryTTH[2] = wasteResponse.type[0];
                    MainActivity.Instance.wetTTH[2] = wasteResponse.type[1];
                    MainActivity.Instance.recycleTTH[2] = wasteResponse.type[2];
                    MainActivity.Instance.badTTH[2] = wasteResponse.type[3];
                    MainActivity.Instance.totalTTH[2] = wasteResponse.sum;
                }
                Log.d("WasteResponse code", String.valueOf(code));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void updateWeekWaste()  //根据收到的数据更新界面
        {
            try {
                URL url = new URL(IP + CommonParameter.route_history + data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(false);         //是否输入参数
                connection.setDoInput(true);        //是否读取参数
                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_weekwaste);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");               //设置请求属性
//            connection.setRequestProperty("User-Agent", "Autoyol_gpsCenter");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int code = connection.getResponseCode();

                if (code == HttpURLConnection.HTTP_OK) {
                    //得到结果
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    char[] buffer = new char[1024];
                    int len = inputStreamReader.read(buffer);
                    String readData = new String(buffer, 0, len);
                    Log.d("WeekWasteResponse", "content:" + readData);
                    Gson gson = new Gson();
                    WeekWasteResponse weekWasteResponse = gson.fromJson(readData, WeekWasteResponse.class);

                    MainActivity.Instance.dryTTH[1] = weekWasteResponse.type[0];
                    MainActivity.Instance.wetTTH[1] = weekWasteResponse.type[1];
                    MainActivity.Instance.recycleTTH[1] = weekWasteResponse.type[2];
                    MainActivity.Instance.badTTH[1] = weekWasteResponse.type[3];
                    MainActivity.Instance.totalTTH[1] = weekWasteResponse.sum;
//
//
//                        HistoryResponse historyResponse;
//
//                        //将得到的更新回应转为数据
//                        historyResponse = gson.fromJson(readData, HistoryResponse.class);
//
//                        //更新数据
//                        int[] temp = MainActivity.Instance.recycleTH;
//                        temp[0] = historyResponse.trashHistory0.type0num;
//                        temp[1] = historyResponse.trashHistory1.type0num;
//                        temp[2] = historyResponse.trashHistory2.type0num;
//                        temp[3] = historyResponse.trashHistory3.type0num;
//                        temp[4] = historyResponse.trashHistory4.type0num;
//                        temp[5] = historyResponse.trashHistory5.type0num;
//                        temp[6] = historyResponse.trashHistory6.type0num;
//                        temp = MainActivity.Instance.badTH;
//                        temp[0] = historyResponse.trashHistory0.type1num;
//                        temp[1] = historyResponse.trashHistory1.type1num;
//                        temp[2] = historyResponse.trashHistory2.type1num;
//                        temp[3] = historyResponse.trashHistory3.type1num;
//                        temp[4] = historyResponse.trashHistory4.type1num;
//                        temp[5] = historyResponse.trashHistory5.type1num;
//                        temp[6] = historyResponse.trashHistory6.type1num;
//                        temp = MainActivity.Instance.wetTH;
//                        temp[0] = historyResponse.trashHistory0.type2num;
//                        temp[1] = historyResponse.trashHistory1.type2num;
//                        temp[2] = historyResponse.trashHistory2.type2num;
//                        temp[3] = historyResponse.trashHistory3.type2num;
//                        temp[4] = historyResponse.trashHistory4.type2num;
//                        temp[5] = historyResponse.trashHistory5.type2num;
//                        temp[6] = historyResponse.trashHistory6.type2num;
//                        temp = MainActivity.Instance.dryTH;
//                        temp[0] = historyResponse.trashHistory0.type3num;
//                        temp[1] = historyResponse.trashHistory1.type3num;
//                        temp[2] = historyResponse.trashHistory2.type3num;
//                        temp[3] = historyResponse.trashHistory3.type3num;
//                        temp[4] = historyResponse.trashHistory4.type3num;
//                        temp[5] = historyResponse.trashHistory5.type3num;
//                        temp[6] = historyResponse.trashHistory6.type3num;
                    inputStreamReader.close();
                }
                Log.d("WeekWasteResponse code", String.valueOf(code));
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void updateRanking()
        {
            try {

                URL url = new URL(IP + CommonParameter.route_ranking + data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(false);         //是否输入参数
                connection.setDoInput(true);        //是否读取参数
                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_ranking);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");               //设置请求属性
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    //得到结果
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    char[] buffer = new char[1024];
                    int len = reader.read(buffer);

                    reader.close();
                    String readData = new String(buffer, 0, len);
                    Log.d("RankingResponse", "content:" + readData);
                    Gson gson = new Gson();
                    RankingResponse rankingResponse;

                    //将得到的更新回应转为数据
                    rankingResponse = gson.fromJson(readData, RankingResponse.class);
                    //更新数据和界面
                    MainActivity.Instance.userRankings = new UserRanking[rankingResponse.user_sum];
                    UserRanking[] myRanking = MainActivity.Instance.userRankings;
                    for( int i=0; i<rankingResponse.user_sum; i++)
                    {
                        UserRanking tempRanking = new UserRanking(rankingResponse.user_scores[i].ranking
                                                , rankingResponse.user_scores[i].user_name
                                                , rankingResponse.user_scores[i].score);
                        myRanking[rankingResponse.user_scores[i].ranking -1] = tempRanking;
                    }


                }
                Log.d("RankingResponse code", String.valueOf(code));


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void updateBinStates()
        {
            try {

                URL url = new URL(IP + CommonParameter.route_state + data);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(false);         //是否输入参数
                connection.setDoInput(true);        //是否读取参数
                connection.setUseCaches(false);
                connection.setRequestMethod(CommonParameter.method_state);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");               //设置请求属性
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    //得到结果
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    char[] buffer = new char[1024];
                    int len = 0, lineLen;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("");
                    lineLen = reader.read(buffer);
                    len = lineLen;
                    String temp  = new String(buffer, 0 ,lineLen);
                    stringBuilder.append(temp);
                    while(lineLen == 1024)
                    {
                        lineLen = reader.read(buffer);
                        len += lineLen;
                        stringBuilder.append(new String(buffer, 0 ,lineLen));

                    }


                    reader.close();
                    String readData = stringBuilder.substring(0);
                    Log.d("BinStatesResponse", "content:" + readData);
                    Gson gson = new Gson();
                    BinStateResponse binStateResponse;


                    //将得到的更新回应转为数据
                    binStateResponse = gson.fromJson(readData, BinStateResponse.class);

                    int setLen = 0;
                    if(binStateResponse.bin_status != null)
                    {
                        //更新数据和界面
                        Set<Map.Entry<String, Integer>> set = binStateResponse.bin_status.entrySet();
                        setLen = set.size();
                        MainActivity.Instance.binStates = new BinState[setLen];
                        BinState[] binStates = MainActivity.Instance.binStates;


                        Log.d("temp", "map len:" + setLen);
                        int i = 0;
                        for(Map.Entry entry : set)
                        {
                            BinState binState = new BinState((String)entry.getKey(),(Integer) entry.getValue());
                            binStates[i] = binState;
                            i++;
                            Log.d("temp", "key:"+ (String)entry.getKey() + "value:"+(Integer)entry.getValue());
                        }
                    }
                    else
                    {
                        MainActivity.Instance.binStates = new BinState[0];
                    }
                }
                Log.d("BinStatesResponse code", String.valueOf(code));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



