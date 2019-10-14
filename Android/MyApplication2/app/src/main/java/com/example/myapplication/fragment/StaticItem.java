package com.example.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

public class StaticItem extends Fragment {


    TextView tv_history, tv_today;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_statics_item,container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_history = view.findViewById(R.id.tv_history);
        tv_today = view.findViewById(R.id.tv_today);

    }

    public void setTv_history(int n)
    {
        tv_history.setText(n);
    }
    public void setTv_today(int n)
    {
        tv_today.setText(n);
    }
}
