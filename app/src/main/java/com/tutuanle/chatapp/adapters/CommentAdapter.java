package com.tutuanle.chatapp.adapters;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.databinding.ItemUserCommentBinding;
import com.tutuanle.chatapp.models.Comment;
import java.util.ArrayList;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    Context context;
    ArrayList<Comment> comments;

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.binding.comment.setText(comment.getMessage());
        holder.binding.profile.setImageBitmap(getBitmapFromEnCodedString(comment.getImage()));
        holder.binding.username.setText(comment.getName());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ItemUserCommentBinding binding;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemUserCommentBinding.bind(itemView);
        }
    }

    private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
        byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
