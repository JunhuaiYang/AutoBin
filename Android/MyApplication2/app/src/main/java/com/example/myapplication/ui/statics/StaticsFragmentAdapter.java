package com.example.myapplication.ui.statics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class StaticsFragmentAdapter extends RecyclerView.Adapter<StaticsFragmentAdapter.ViewHolder> {

    private Context mContext;

    private ViewHolder[] viewHolders = new ViewHolder[6];

    public StaticsFragmentAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public StaticsFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        switch (viewType) {
            case 0:
                holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_statics_item, parent, false));
//                Log.d("holder0", "1");
                break;
            case 1:
                holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_statics_total, parent, false));
                break;
            case 2:
                holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_statics_item_chart, parent, false));
//                Log.d("holder1", "1");
                break;
            case 3:
                holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_statics_item_label, parent, false));
//                Log.d("holder2", "1");
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StaticsFragmentAdapter.ViewHolder holder, int position) {

        switch (position) {
            case 0: //可回收垃圾
                holder.tv_trashtype.setText("可回收垃圾");
                holder.tv_trashtype.setTextColor(mContext.getResources().getColor(R.color.colorRecycleTrash));
                holder.tv_today.setText(String.valueOf(MainActivity.Instance.recycleTTH[0]));
                holder.tv_week.setText(String.valueOf(MainActivity.Instance.recycleTTH[1]));
                holder.tv_history.setText(String.valueOf(MainActivity.Instance.recycleTTH[2]));
                holder.imageView.setImageResource(R.mipmap.kehuishou);
                viewHolders[0] = holder;
                break;
            case 1: //有害垃圾
                holder.tv_trashtype.setText("有害垃圾");
                holder.tv_trashtype.setTextColor(mContext.getResources().getColor(R.color.colorBadTrash));
                holder.tv_today.setText(String.valueOf(MainActivity.Instance.badTTH[0]));
                holder.tv_week.setText(String.valueOf(MainActivity.Instance.badTTH[1]));
                holder.tv_history.setText(String.valueOf(MainActivity.Instance.badTTH[2]));
                holder.imageView.setImageResource(R.mipmap.youhai);
                viewHolders[1] = holder;
                break;
            case 2: //湿垃圾
                holder.tv_trashtype.setText("湿垃圾");
                holder.tv_trashtype.setTextColor(mContext.getResources().getColor(R.color.colorWetTrash));
                holder.tv_today.setText(String.valueOf(MainActivity.Instance.wetTTH[0]));
                holder.tv_week.setText(String.valueOf(MainActivity.Instance.wetTTH[1]));
                holder.tv_history.setText(String.valueOf(MainActivity.Instance.wetTTH[2]));
                holder.imageView.setImageResource(R.mipmap.shi);
                viewHolders[2] = holder;
                break;
            case 3: //干垃圾
                holder.tv_trashtype.setText("干垃圾");
                holder.tv_trashtype.setTextColor(mContext.getResources().getColor(R.color.colorDryTrash));
                holder.tv_today.setText(String.valueOf(MainActivity.Instance.dryTTH[0]));
                holder.tv_week.setText(String.valueOf(MainActivity.Instance.dryTTH[1]));
                holder.tv_history.setText(String.valueOf(MainActivity.Instance.dryTTH[2]));
                holder.imageView.setImageResource(R.mipmap.gan);
                viewHolders[3] = holder;
                break;
            case 4:
                holder.tv_today.setText(String.valueOf(MainActivity.Instance.totalTTH[0]));
                holder.tv_week.setText(String.valueOf(MainActivity.Instance.totalTTH[1]));
                holder.tv_history.setText(String.valueOf(MainActivity.Instance.totalTTH[2]));
                viewHolders[4] = holder;

            case 5: //表格

                viewHolders[5] = holder;

                break;
            case 6: //可回收垃圾
                holder.tv_title.setText("可回收垃圾");
                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.colorRecycleTrash));
                holder.tv_content.setText("可回收垃圾是指，适宜回收利用和资源化利用的生活废弃物，如废纸张、废塑料、废玻璃制品、废金属、废织物等");
                break;
            case 7: //有害垃圾

                holder.tv_title.setText("有害垃圾");
                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.colorBadTrash));
                holder.tv_content.setText("有害垃圾是指，对人体健康或者自然环境造成直接或潜在危害的废弃物\n主要包括：\n" +
                        "废电池（充电电池、铅酸电池、镍镉电池、纽扣电池等）、废油漆、消毒剂、荧光灯管、含汞温度计、废药品及其包装物等");
                break;
            case 8: //湿垃圾

                holder.tv_title.setText("湿垃圾");
                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.colorWetTrash));
                holder.tv_content.setText("湿垃圾是指，日常生活垃圾产生的容易腐烂的生物质废弃物;\n 主要包括：\n" +
                        "食材废料、剩饭剩菜、过期食品、蔬菜水果、瓜皮果核、花卉绿植、中药残渣等");
                break;
            case 9: //干垃圾

                holder.tv_title.setText("干垃圾");
                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.colorDryTrash));
                holder.tv_content.setText("干垃圾是指，除可回收物、有害垃圾、湿垃圾以外的其它生活废弃物;\n主要包括：\n" +
                        "餐盒、餐巾纸、湿纸巾、卫生间用纸、塑料袋、食品包装袋、污染严重的纸、烟蒂、纸尿裤、一次性杯子、大骨头、贝壳、花盆等");
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        if (position < 4) {
            return 0;
        } else if (position == 4) {
            return 1;
        } else if (position == 5) {
            return 2;
        } else {
            return 3;
        }
//        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 5 + 1 + 4;
    }

    public void updateView() //更新视图
    {
        for (int i = 0; i < 5; i++) {
            viewHolders[i].update(i);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_trashtype, tv_today, tv_history, tv_week;

        LineChart lineChart;

        TextView tv_title, tv_content;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            switch (itemView.getId()) {
                case R.id.fragment_statics_item:
                    imageView = itemView.findViewById(R.id.image);
                    tv_trashtype = itemView.findViewById(R.id.tv_trashtype);
                    tv_today = itemView.findViewById(R.id.tv_today);
                    tv_history = itemView.findViewById(R.id.tv_history);
                    tv_week = itemView.findViewById(R.id.tv_week);
                    break;
                case R.id.fragment_statics_total:
                    tv_today = itemView.findViewById(R.id.tv_today);
                    tv_history = itemView.findViewById(R.id.tv_history);
                    tv_week = itemView.findViewById(R.id.tv_week);
                    break;
                case R.id.fragment_statics_item_chart:


                    lineChart = itemView.findViewById(R.id.lineChart);
                    showLineChart(lineChart, getLineChartData());   //更新表格
                    break;
                case R.id.fragment_statics_item_label:
                    tv_title = itemView.findViewById(R.id.tv_title);
                    tv_content = itemView.findViewById(R.id.tv_content);
                    break;
                default:
                    Log.d("TEST", "no fragment_statics_item");
                    break;
            }


        }

        public void update(int position) {
            switch (view.getId()) {
                case R.id.fragment_statics_item:
                    switch (position) {
                        case 0: //可回收垃圾
                            tv_today.setText(String.valueOf(MainActivity.Instance.recycleTTH[0]));
                            tv_week.setText(String.valueOf(MainActivity.Instance.recycleTTH[1]));
                            tv_history.setText(String.valueOf(MainActivity.Instance.recycleTTH[2]));
//                            Log.d("temp" , "可回收垃圾历史总共所仍垃圾数" + MainActivity.Instance.recycleTTH[1]);
                            break;
                        case 1: //有害垃圾
                            tv_today.setText(String.valueOf(MainActivity.Instance.badTTH[0]));
                            tv_week.setText(String.valueOf(MainActivity.Instance.badTTH[1]));
                            tv_history.setText(String.valueOf(MainActivity.Instance.badTTH[2]));
                            break;
                        case 2: //湿垃圾
                            tv_today.setText(String.valueOf(MainActivity.Instance.wetTTH[0]));
                            tv_week.setText(String.valueOf(MainActivity.Instance.wetTTH[1]));
                            tv_history.setText(String.valueOf(MainActivity.Instance.wetTTH[2]));
                            break;
                        case 3: //干垃圾
                            tv_today.setText(String.valueOf(MainActivity.Instance.dryTTH[0]));
                            tv_week.setText(String.valueOf(MainActivity.Instance.dryTTH[1]));
                            tv_history.setText(String.valueOf(MainActivity.Instance.dryTTH[2]));
                            break;

                    }
                    break;
                case R.id.fragment_statics_total:
                    tv_today.setText(String.valueOf(MainActivity.Instance.totalTTH[0]));
                    tv_week.setText(String.valueOf(MainActivity.Instance.totalTTH[1]));
                    tv_history.setText(String.valueOf(MainActivity.Instance.totalTTH[2]));
                    break;
                case R.id.fragment_statics_item_chart:
                    showLineChart(lineChart, getLineChartData());   //更新表格
                    break;
                case R.id.fragment_statics_item_label:
                    break;
            }
        }

        private List<ILineDataSet> getLineChartData() {
            //
            List<Entry> dataList = new ArrayList();
            List<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
            for (int i = 1; i <= 7; i++) {
                dataList.add(new Entry(i, MainActivity.Instance.recycleTH[i - 1]));
            }

            LineDataSet dataSet = new LineDataSet(dataList, "可回收垃圾");
            dataSet.setColor(MainActivity.Instance.getResources().getColor(R.color.colorRecycleTrash));
            lineDataSets.add(dataSet);

            //
            List<Entry> dataList2 = new ArrayList();
            for (int i = 1; i <= 7; i++) {
                dataList2.add(new Entry(i, MainActivity.Instance.badTH[i - 1]));
            }

            LineDataSet dataSet2 = new LineDataSet(dataList2, "有害垃圾");
            dataSet2.setColor(MainActivity.Instance.getResources().getColor(R.color.colorBadTrash));
            lineDataSets.add(dataSet2);

            //
            List<Entry> dataList3 = new ArrayList();
            for (int i = 1; i <= 7; i++) {
                dataList3.add(new Entry(i, MainActivity.Instance.wetTH[i - 1]));
            }

            LineDataSet dataSet3 = new LineDataSet(dataList3, "湿垃圾");
            dataSet3.setColor(MainActivity.Instance.getResources().getColor(R.color.colorWetTrash));
            lineDataSets.add(dataSet3);
            //
            List<Entry> dataList4 = new ArrayList();
            for (int i = 1; i <= 7; i++) {
                dataList4.add(new Entry(i, MainActivity.Instance.dryTH[i - 1]));
            }

            LineDataSet dataSet4 = new LineDataSet(dataList4, "干垃圾");
            dataSet4.setColor(MainActivity.Instance.getResources().getColor(R.color.colorDryTrash));
            lineDataSets.add(dataSet4);


            return lineDataSets;
        }

        private void showLineChart(LineChart lineChart, List<ILineDataSet> list) {


            LineData lineData = new LineData(list);

            lineChart.setData(lineData);

        }
    }
}
