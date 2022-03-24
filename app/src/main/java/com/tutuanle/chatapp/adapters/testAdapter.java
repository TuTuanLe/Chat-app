package com.tutuanle.chatapp.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class testAdapter  extends  RecyclerView.Adapter<testAdapter.TestAdapterViewHolder> {

    @NonNull
    @Override
    public TestAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TestAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public  class    TestAdapterViewHolder extends RecyclerView.ViewHolder{

    public TestAdapterViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
}
