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
import com.tutuanle.chatapp.databinding.ItemUserCheckedRecycleBinding;
import com.tutuanle.chatapp.interfaces.UserListener;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.util.List;


public class UserCheckedAdapter extends RecyclerView.Adapter<UserCheckedAdapter.UserCheckedViewHolder> {

    private final List<User> users;
    private PreferenceManager preferenceManager;

    public UserCheckedAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserCheckedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserCheckedRecycleBinding itemUserBinding = ItemUserCheckedRecycleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new UserCheckedViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCheckedViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserCheckedViewHolder extends RecyclerView.ViewHolder {

        ItemUserCheckedRecycleBinding binding;

        public UserCheckedViewHolder(ItemUserCheckedRecycleBinding itemUserBinding) {
            super(itemUserBinding.getRoot());
            binding = itemUserBinding;
        }

        void setUserData(User user) {
            binding.username.setText(user.getName());
            binding.profile.setImageBitmap(getUserImage(user.getProfileImage()));

        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}