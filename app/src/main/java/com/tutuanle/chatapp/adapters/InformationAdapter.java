package com.tutuanle.chatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tutuanle.chatapp.databinding.ItemImageBinding;
import java.util.List;


public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.InformationViewHolder> {

    private  List<String> images;

    public InformationAdapter(List<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public InformationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageBinding itemImageBinding = ItemImageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new InformationViewHolder(itemImageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull InformationViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class InformationViewHolder extends RecyclerView.ViewHolder {

        ItemImageBinding binding;

        public InformationViewHolder(ItemImageBinding itemImageBinding) {
            super(itemImageBinding.getRoot());
            binding = itemImageBinding;
        }

        void setUserData(String item) {

        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}