package com.tutuanle.chatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.databinding.ItemSendRequestFriendBinding;
import com.tutuanle.chatapp.interfaces.RequestListener;
import com.tutuanle.chatapp.models.RequestFriend;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private PreferenceManager preferenceManager;
    private final List<RequestFriend> requestFriends;
    public  final RequestListener requestListener;

    public RequestAdapter(List<RequestFriend> requestFriends, RequestListener requestListener){
        this.requestFriends = requestFriends;
        this.requestListener = requestListener;
    }
    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_request_friend, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        holder.setData(requestFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return requestFriends.size();
    }

    public  class RequestViewHolder extends RecyclerView.ViewHolder {
        private ItemSendRequestFriendBinding binding;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendRequestFriendBinding.bind(itemView);
        }
        public  void setData(RequestFriend requestFriend){
            binding.textRequest.setText(requestFriend.getRequest());
            binding.imgAvatar.setImageBitmap(getBitmapFromEnCodedString(requestFriend.getImage()));
            binding.textUserRequest.setText(requestFriend.getName());
            if(requestFriend.getRequest().equals("ACCEPT")){
                binding.textRequest.setTextColor(binding.getRoot().getResources().getColor(R.color.blue));
                binding.iconRequest.setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(), R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.layoutRequest.setOnClickListener(v->requestListener.onAcceptFiend(requestFriend.getSender()));
            }else{
                binding.textRequest.setTextColor(binding.getRoot().getResources().getColor(R.color.icon_background));
                binding.iconRequest.setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(), R.color.icon_background), android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.layoutRequest.setOnClickListener(v->requestListener.onCancelRequestFriend(requestFriend.getRequestUid()));
            }
        }
        private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
            byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }
}
