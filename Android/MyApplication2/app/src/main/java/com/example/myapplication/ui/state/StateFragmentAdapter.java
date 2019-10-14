package com.example.myapplication.ui.state;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
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
