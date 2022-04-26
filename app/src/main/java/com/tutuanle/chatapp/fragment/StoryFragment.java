package com.tutuanle.chatapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.adapters.HomeFriendAdapter;
import com.tutuanle.chatapp.adapters.TopStatusAdapter;
import com.tutuanle.chatapp.models.Status;
import com.tutuanle.chatapp.models.UserStatus;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private MainScreenActivity mainScreenActivity;
    private PreferenceManager preferenceManager;
    private TopStatusAdapter statusAdapter;

    private ArrayList<UserStatus> userStatuses;
    public StoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoryFragment newInstance(String param1, String param2) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_story, container, false);
        mainScreenActivity = (MainScreenActivity) getActivity();
        assert mainScreenActivity != null;
        preferenceManager = mainScreenActivity.preferenceManager;

        getUserStatus();
        return view;
    }

    private void getUserStatus() {
        userStatuses = new ArrayList<>();

        statusAdapter = new TopStatusAdapter(mainScreenActivity, userStatuses);
        RecyclerView temp = view.findViewById(R.id.statusList);
        temp.setLayoutManager(new GridLayoutManager(mainScreenActivity, 2));

        temp.setAdapter(statusAdapter);

        ArrayList<Status> statuses = new ArrayList<>();


        statuses.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648451488181?alt=media&token=9e1f4eb7-8825-43c2-8220-106c92409620",
                164845148
        ));
        statuses.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648451488181?alt=media&token=9e1f4eb7-8825-43c2-8220-106c92409620",
                164853774
        ));


        ArrayList<Status> statuses1 = new ArrayList<>();


        statuses1.add(new Status(
                "hhttps://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                164845148
        ));
        statuses1.add(new Status(
                "https://www.w3schools.com/w3images/fjords.jpg",
                164853774
        ));
        statuses1.add(new Status(
                "https://st.depositphotos.com/1006706/2671/i/600/depositphotos_26715369-stock-photo-which-way-to-choose-3d.jpg",
                164853774
        ));

        ArrayList<Status> statuses2 = new ArrayList<>();


        statuses2.add(new Status(
                "https://www.perma-horti.com/wp-content/uploads/2019/02/image-2.jpg",
                164845148
        ));


        userStatuses.add(new UserStatus(
                "Tuan Anh",
                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648537741702?alt=media&token=685fb7d8-a05d-429d-9834-1db74b41ff0e",
                164853810,
                statuses
        ));
        userStatuses.add(new UserStatus(
                "TuTuanLe",
                "https://www.w3schools.com/w3images/fjords.jpg",
                174853810,
                statuses1
        ));
        userStatuses.add(new UserStatus(
                "TAnh",
                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                184853810,
                statuses2
        ));

        userStatuses.add(new UserStatus(
                "Tram Huynh",
                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                9,
                statuses2
        ));
        userStatuses.add(new UserStatus(
                "Phuong",
                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                9,
                statuses2
        ));
        userStatuses.add(new UserStatus(
                "Nhon",
                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                9,
                statuses2
        ));



        statusAdapter.notifyDataSetChanged();
    }

}