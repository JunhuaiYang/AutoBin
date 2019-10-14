package com.example.myapplication.ui.statics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.fragment.StaticItem;

public class StaticsFragment extends Fragment {

    public static StaticsFragment _instance;
    RecyclerView recyclerView;
    private StaticsViewModel staticsViewModel;
    Fragment recycleItem, badTrashItem, wetTrashItem, dryTrashItem;


    private StaticsFragmentAdapter staticsFragmentAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        staticsViewModel = ViewModelProviders.of(this).get(StaticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statics, container, false);
        _instance = this;


        recyclerView = root.findViewById(R.id.rv_statics);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        staticsFragmentAdapter = new StaticsFragmentAdapter(getContext());
        recyclerView.setAdapter(staticsFragmentAdapter);
//        final TextView textView = root.findViewById(R.id.text_gallery);
//        staticsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    public void updateView()
    {
        staticsFragmentAdapter.updateView();
    }
}