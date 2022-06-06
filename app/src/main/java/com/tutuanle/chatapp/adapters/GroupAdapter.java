package com.tutuanle.chatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tutuanle.chatapp.databinding.ItemContainerUserBinding;
import com.tutuanle.chatapp.databinding.ItemUserBinding;
import com.tutuanle.chatapp.databinding.ItemUserCheckedBinding;
import com.tutuanle.chatapp.interfaces.UserListener;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.util.List;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final List<User> users;
    private final UserListener userListener;
    private PreferenceManager preferenceManager;

    public GroupAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }


    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserCheckedBinding itemUserBinding = ItemUserCheckedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new GroupViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        ItemUserCheckedBinding binding;

        public GroupViewHolder(ItemUserCheckedBinding itemUserCheckedBinding) {
            super(itemUserCheckedBinding.getRoot());
            binding = itemUserCheckedBinding;
        }

        void setUserData(User user) {
            binding.username.setText(user.getName());
            binding.profile.setImageBitmap(getUserImage(user.getProfileImage()));
            if(user.getAvailability() == 1){
                binding.statusAvailability.setVisibility(View.VISIBLE);
            }else {
                binding.statusAvailability.setVisibility(View.GONE);
            }
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}