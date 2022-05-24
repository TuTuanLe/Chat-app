package com.tutuanle.chatapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.databinding.ItemStatusStoryBinding;
import com.tutuanle.chatapp.models.Status;
import com.tutuanle.chatapp.models.UserStatus;
import java.util.ArrayList;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;
    private int selectedItem;

    public StoryAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
        selectedItem = -1;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status_story, parent, false);

        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserStatus userStatus = userStatuses.get(position);

        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() -1);

        Glide.with(context).load(lastStatus.getImageUrl()).into(holder.binding.image);
        Glide.with(context).load(lastStatus.getImageUrl()).into(holder.binding.imageUserStatus);
        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());

        if(selectedItem == position){
            holder.binding.frameLayout.setBackgroundResource(R.drawable.button_bg);
        }else{
            holder.binding.frameLayout.setBackgroundColor(0);
        }
        holder.binding.frameLayout.setOnClickListener(v-> {
            int previousItem = selectedItem;
            selectedItem = position;

            notifyItemChanged(previousItem);
            notifyItemChanged(position);
        });

        holder.binding.circularStatusView.setOnClickListener(view -> {
            ArrayList<MyStory> myStories = new ArrayList<>();
            for (Status status : userStatus.getStatuses()) {
                myStories.add(new MyStory(status.getImageUrl()));
            }
            new StoryView.Builder(((MainScreenActivity)context).getSupportFragmentManager())
                    .setStoriesList(myStories) // Required
                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                    .setTitleText(userStatus.getName()) // Default is Hidden
                    .setSubtitleText("") // Default is Hidden
                    .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                    .setStoryClickListeners(new StoryClickListeners() {
                        @Override
                        public void onDescriptionClickListener(int position1) {
                            //your action
                        }

                        @Override
                        public void onTitleIconClickListener(int position1) {
                            //your action
                        }
                    }) // Optional Listeners
                    .build() // Must be called before calling show method
                    .show();

        });

    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ItemStatusStoryBinding binding;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusStoryBinding.bind(itemView);
        }
    }
}
