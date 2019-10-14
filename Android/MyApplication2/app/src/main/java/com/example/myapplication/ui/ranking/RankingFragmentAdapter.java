package com.example.myapplication.ui.ranking;

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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class RankingFragmentAdapter extends RecyclerView.Adapter<RankingFragmentAdapter.ViewHolder> {

    private Context mContext;


    RankingFragmentAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RankingFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_ranking_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankingFragmentAdapter.ViewHolder holder, int position) {

        if(MainActivity.Instance.userRankings == null || MainActivity.Instance.userRankings.length <= 0)
        {
            return;
        }
        holder.tv_ranking.setText(String.valueOf(MainActivity.Instance.userRankings[position].ranking));
        holder.tv_username.setText(MainActivity.Instance.userRankings[position].user_name);
        holder.tv_score.setText(String.valueOf(MainActivity.Instance.userRankings[position].score));
    }


    @Override
    public int getItemCount() {
        return MainActivity.Instance.userRankings.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ranking, tv_username, tv_score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_ranking = itemView.findViewById(R.id.tv_number);
            tv_username = itemView.findViewById(R.id.tv_name);
            tv_score = itemView.findViewById(R.id.tv_score);
        }
    }
}
