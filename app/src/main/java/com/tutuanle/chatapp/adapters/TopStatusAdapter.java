package com.tutuanle.chatapp.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tutuanle.chatapp.databinding.ItemStatusBinding;

public class TopStatusAdapter  extends   RecyclerView.Adapter<TopStatusAdapter.TopStatusAdapterViewHolder> {
    @NonNull
    @Override
    public TopStatusAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TopStatusAdapterViewHolder extends RecyclerView.ViewHolder{
        ItemStatusBinding binding;
        public TopStatusAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding
        }
    }
}
