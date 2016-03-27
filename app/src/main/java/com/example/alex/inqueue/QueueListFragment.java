package com.example.alex.inqueue;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class QueueListFragment extends Fragment {
    private List<Queue> queues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_queue_list, container, false);
        initData();
        RecyclerView rv = (RecyclerView)view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(queues);
        rv.setAdapter(adapter);
        return view;
    }

    private void initData() {
        queues = new ArrayList<>();
        queues.add(new Queue("РК2. Индексы", "длинное описание об очереди", "2-я Бауманская", 17, 9, 3));
        queues.add(new Queue("РК2. Индексы", "длинное описание об очереди", "2-я Бауманская", 17, 9, 3));
        queues.add(new Queue("РК2. Индексы", "длинное описание об очереди", "2-я Бауманская", 17, 9, 3));
    }
}