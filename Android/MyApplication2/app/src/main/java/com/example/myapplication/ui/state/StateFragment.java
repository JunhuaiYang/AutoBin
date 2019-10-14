package com.example.myapplication.ui.state;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.statics.StaticsFragmentAdapter;

public class StateFragment extends Fragment {

    public static StateFragment _instance;
    private StateFragmentAdapter adapter;
    private StateViewModel stateViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        _instance = this;
        stateViewModel =
                ViewModelProviders.of(this).get(StateViewModel.class);
        View root = inflater.inflate(R.layout.fragment_state, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.recycleView);
        adapter = new StateFragmentAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
//        stateViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
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