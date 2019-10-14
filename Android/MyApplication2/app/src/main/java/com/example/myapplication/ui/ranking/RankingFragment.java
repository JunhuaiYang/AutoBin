package com.example.myapplication.ui.ranking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class RankingFragment extends Fragment {

    public static RankingFragment _instance;
    private RankingViewModel rankingViewModel;
    private RankingFragmentAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        _instance = this;
        rankingViewModel =
                ViewModelProviders.of(this).get(RankingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ranking, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.recycleView);

        adapter = new RankingFragmentAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroy() {
        _instance = null;
        super.onDestroy();
    }

    public void updateView()
    {
        adapter.notifyDataSetChanged();
    }
}