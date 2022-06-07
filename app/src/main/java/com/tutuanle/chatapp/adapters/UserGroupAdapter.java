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
import com.tutuanle.chatapp.databinding.ItemUserFriendBinding;
import com.tutuanle.chatapp.interfaces.UserListener;
import com.tutuanle.chatapp.interfaces.UsersListener;
import com.tutuanle.chatapp.models.User;

import java.util.List;


public class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.UserGroupViewHolder> {

    private final List<User> users;
    private final UsersListener userListener;

    public UserGroupAdapter(List<User> users, UsersListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserFriendBinding itemUserFriendBinding = ItemUserFriendBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new UserGroupViewHolder(itemUserFriendBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserGroupViewHolder extends RecyclerView.ViewHolder {

        ItemUserFriendBinding binding;

        public UserGroupViewHolder(ItemUserFriendBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user) {
            binding.username.setText(user.getName());
            binding.profile.setImageBitmap(getUserImage(user.getProfileImage()));
            if(user.getAvailability() == 1){
                binding.statusAvailability.setVisibility(View.VISIBLE);
            }else {
                binding.statusAvailability.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> userListener.onUsersClicked(user));
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}