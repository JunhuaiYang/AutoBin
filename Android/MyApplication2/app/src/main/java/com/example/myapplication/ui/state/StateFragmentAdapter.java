package com.example.myapplication.ui.state;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.activity.ui.SettingActivity;
import com.example.myapplication.ui.ranking.RankingFragmentAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

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
    public void onBindViewHolder(@NonNull StateFragmentAdapter.ViewHolder holder, int position) {

        if(MainActivity.Instance.binStates == null || MainActivity.Instance.binStates.length <= 0)
        {
            return;
        }
        holder.tv_name.setText(MainActivity.Instance.binStates[position].BinName);
        if(MainActivity.Instance.binStates[position].BinStatus == 1)
        {
            holder.tv_state.setText("正常");
            holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
            holder.tv_state.setOnClickListener(new View.OnClickListener() {  //设置垃圾桶状态钮按点击后的反应
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    View view1 = LayoutInflater.from(mContext).inflate(R.layout.layout_bin_control, null);
                    final TextView tv_angle = view1.findViewById(R.id.tv_angle);
                    final TextView tv_temp = view1.findViewById(R.id.tv_angle);
                    Button btn1_add = view1.findViewById(R.id.btn1_add);
                    Button btn1_sub = view1.findViewById(R.id.btn1_sub);
                    Button btn2_add = view1.findViewById(R.id.btn2_add);
                    Button btn2_sub = view1.findViewById(R.id.btn2_sub);
                    Button btn3_add = view1.findViewById(R.id.btn3_add);
                    Button btn3_sub = view1.findViewById(R.id.btn3_sub);
                    Button btn4_add = view1.findViewById(R.id.btn4_add);
                    Button btn4_sub = view1.findViewById(R.id.btn4_sub);
                    builder.setTitle("手动模式").setView(view1)
                            .show();
                }
            });
        }
        else
        {
            holder.tv_state.setText("异常");
            holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.colorRed));
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_state = itemView.findViewById(R.id.tv_state);
        }
    }
}
