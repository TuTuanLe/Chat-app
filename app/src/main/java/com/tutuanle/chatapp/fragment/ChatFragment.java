package com.tutuanle.chatapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.CircularDotsLoader;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.activities.ProfileActivity;
import com.tutuanle.chatapp.activities.SearchActivity;
import com.tutuanle.chatapp.adapters.RequestAdapter;
import com.tutuanle.chatapp.adapters.Users_Adapter;
import com.tutuanle.chatapp.models.RequestFriend;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private View view;

    private MainScreenActivity mainScreenActivity;
    private PreferenceManager preferenceManager;
    private  FirebaseFirestore firebaseFirestore;

    public ChatFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mainScreenActivity = (MainScreenActivity) getActivity();
        assert mainScreenActivity != null;
        preferenceManager = mainScreenActivity.preferenceManager;

        initialData();
        getUSer();
        setListener();
        getRequestFriend();
        return view;
    }

    private void setListener() {
        view.findViewById(R.id.search_friend).setOnClickListener(v ->
                startActivity(new Intent(mainScreenActivity, SearchActivity.class)));
    }

    private void getUSer() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                            user.setProfileImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                            user.setToken(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                            user.setUid(queryDocumentSnapshot.getId());
                            user.setAvailability(queryDocumentSnapshot.getLong(Constants.KEY_AVAILABILITY));
                            users.add(user);

                        }
                        if (users.size() > 0) {
                            Users_Adapter users_adapter = new Users_Adapter(users, mainScreenActivity);

                            RecyclerView temp = view.findViewById(R.id.userRecyclerView);
                            temp.setAdapter(users_adapter);
                            temp.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    }
                });


    }

    private void getRequestFriend(){


        List<RequestFriend> requestFriends = new ArrayList<>();
        requestFriends.add(new RequestFriend("1","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" , "Tu Tuan Le" , "ACCEPT"));
        requestFriends.add(new RequestFriend("4","/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDABALDA4MChAODQ4SERATGCgaGBYWGDEjJR0oOjM9PDkzODdASFxOQERXRTc4UG1RV19iZ2hnPk1xeXBkeFxlZ2P/2wBDARESEhgVGC8aGi9jQjhCY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2P/wAARCACVAJYDASIAAhEBAxEB/8QAGgAAAQUBAAAAAAAAAAAAAAAABQACAwQGAf/EAD0QAAIBAwMCAwUIAAUBCQAAAAECAwAEERIhMQVBE1FhBiJxgZEUMqGxwdHh8BUjJEJSMxZEYmRygqKjsv/EABkBAAIDAQAAAAAAAAAAAAAAAAEDAAIEBf/EACsRAAICAQQCAQIFBQAAAAAAAAECABEDEiExQQRRExQyIiNhcfAFM4Ghsf/aAAwDAQACEQMRAD8AiWVwmjU2k8jJ3qFo3LZznyx2pK2ZADscVZRA3B3rWcWNtxKr5XkYDo9dGXvtpmVBK4Pu40KeT5kVBdXcUefEfSedI5NUbiBipKnjahcysjEGqfSKTZMYnnGq00Zs+l2KGKG6fOp1DqBwMiqV70y6+0TSLFqQsWBUg8+nNDOme0NzZRpDIizxLxqJDAdgD5fL0rQWvtBazhdaSRk88ED6b/hQGN8ZJAiszDL95gYSXFuTGHliIO6glfwqMksSzEknck1qlu7O4QKJonD7aGIyfkaz3UViW/lWHToBGNJ243/HNMR9RqpkyJpFg3L/ALPjPj/+39asdQ6YbuTxFkCsFwARzz3pdAH+ic45kPz2FV5+ryxX0igBolfTpI3GNjilEMXOmNBUYwGnbHpU0F0ssjJhc8b52q+UDXIIO6Icj4kY/wDyakuZHW0eWAoxC6hncEfKqPRTJN9onkOS5Az6gfyKobYajGDShCDuVeo3ksF8VibACjII2Pf9aSdW2xLDvjlTyfhRM3dpIzwyOgIJDK+w2PrtQ7q1pbxwJPAANbf7TsQRn9KutGgRFuWFsrQUkLXE4RFALtwBsP4q/f8AS4YbUyR5BXGcnOd6s9HthHE107YBBAzwAOT+FTrML2wnaNDwygee21FmOrbgSqINO/JmZEJZgFUkngAUxoSCQVwRyKMdHjD3hJH3UJB8jx+9EJJLS4doJCjMDjDefGx86sz0aqVTHqW7qZMxelKjc/SHE+mE5QjILdvTYUqOtfcGh/UExprfJO675zV6LAwByfIU4QNDEyhvvjIQKdx5H6ZxUNu7TSqq7HI2oeOfy69Tf/UP74I7/wCy9Z9Oe5Z5HfQMjT3BHf8Av71X6pa28N2yoMqwDDPb0ox4gSAMRoRAcckgf39Ky0lxJLdySyLp18jyG39zRwFnYt1FeTjVEA7nU6VLcFhbAuQMkEgY+dPHSr623ktnxjPu+8B6nFGvZ8K5lUN7xwSPT0otNf2lm/hTShGxnGkn8hVmzMGKgXFhB8YLHmY8tvvzXQe42rYNHZXwJKwz7aSwwSAfXkVn+p9L+x3UaQEssxwgY75zxn50UyhjR2mfJhKixvFYdSlskdAqurbgHbDef8VVkkMkjO/3mJJPqaKN7PSiMFbhC/cFSB9f4qrL0m+i1EwllXupBz8BzUVkuwZR0y0ARCfRLwSw/Z3b/MT7ue4/irPTI4o4pRA4eMykjHbjb1+NZh0eNykiMjDlWGDT4Lia3bVDIyHbODz8fOqthu6MunkaaDDiFrzo8ryySwuraiW0nY88CqadLumnSN4ygbluQB8qki63dIAHCSb7kjBI+VXYOuQSOBKhiGDvnV+Qqv5ijiH8hjd1L8lur2/gAsqadPu8gVBY2Rs0dPF1qxyBpxj+7UBub2Wa8edHZM7LgkYXyoh0e9mkufBmlLKVOkHck88/DNUONgvMYudGcbR/R4vCmu4+dDBc45xmg0z+LM8mMa2LY+NaiKEwC5KJnU5dRn7xIH65oAvTLtpvDMLA92P3R86sjAkkymVGCqoHuGLY/a7OJ3LAkblWxkjbtSpt7P8A4baRJEFZtlAbuANz+VKlhC24jzlVNmO8HW6COBW1li41Ek8ZqrBGtmJJXOkA6VyPyrtteJBYBThmUlQAd8fpQ6eaS4l94kknAFJbUhZB3OzhxDOqZn6liS7kuMqCRGNgvn8qrOAoznSO7GnBdG2a7XTwp8aATgeT5HzZSw46/aaX2bt1SzNwWy8pwQOAATUfUujXN1eyzxtFpbGAWOdgB5VL0NTBBCgOVdcnfjvVWf2ke0urhJ4ozHG5VcEqdiQM854rN+LWSs0ZFUIof9I7o/TLy3vllmXw0UHPvA6sjjY/P5Ve6pIGurG3UAyGYSYzwB/T9Ki6X7Q23UbgQBWSUgkDkED1+tcTp0kHXop1dpImDMSxJK7YwT35GP4quolraU0hV0r7lrqt5JY2ySxqrEuFIbywf2qpF7RQnPiwSL5aSG/ailzbQ3UfhzprTOcZI3ofN7P2j6jE0kRI2AOQPrv+NBDjqmhyDLdodpQ6z1G3vIYkhUlwdWojGPT8qZ0WwW8ld5lJhQY5Iyf7+lUZrZ471rZffcPoXgat9vhWlmKdH6TpRssPdVscse/98qc34FCr3MqA5HL5OBBPWrGOzljaHZJAfdznBGP3qtaWFxeK7QqMIOScAnyHrRnryLcdMS4j0sFIYN/4T5fhTvZ1WHTmJGA0hI9RgUBkIx33CcIObT1M3T4pXhkWSJirqcgio9RJySST3PetH020tX6QrzxJhslmbY8kc9qc7BRvM+JC7UDUGQdYuoS2phLqOTr3x8Kt/wDaD/y3/wBn8VPe9NsfspniQj/cvhvs2fjnahsPSppYtZZUPYN+9IvE3ImwYvJVbU2P57la4uJLmYyStlj9APIUqZNG0MrRvjUpxtSrQAK2mBib35lI4xgYFKJPeJLb42xU0lnPGqlkO4BwNyM+nyqEHFT40Ztfc6R8jyMWP4DsJJIRnn8abmmlsnk1c6RZNf3yx5IRRqc7bD5+uKYx0izMSqSQJqelJGLC3dSCSg3HnjegXW7C0e+bcZcaiGbIDb5+G29aAdBs9eoKwOkAEMQynGMgih117PztNpS4iKFff1gljz2/XNcpyTx3OwlX+LqZzoVpcwe0cIsjlkJM+o4UpsGI8+dvXFabq3tHZ9Ku0gl1SNpJdY8EodsZyRzv+FDroX3RlAinGh9iQgz8N/ie9Bn6fFeTm4uZtckn/Njvt5/KrMhRQxNyiuMjlVFTQ9aZ+pR2s1lG09uVJWRATnJ8uRxQ5b2+tXCeNMjKMaGJ2HwNT9Au4Oh201rKHctJ4g8MAgZUbZJGeOaXWOoydUuEt7aWeK0EYaTwvddmJIwfQAfjThkKILXaZmwjJkIVt5d9nrNpJGvpiTuQurOSe7Z+o+tEOqdM/wARMZ8Yx6M7acjf6UEsLyTpVpc2ZlaQKqmDWAHUtqB+IGnPzxUEXVb2ENpuXwf+fvfnmoobIfkG0q5TCPiYXNJdWhPRntjlikWBp5Yrx9cCobZWt/ZzdsN4LMCO2ckfmKGxe1oW9jt5bUtAzaDOXwRjltOOOT8N9uKMxxx3PTXtlIAUNDuckadgT9AaWGsf5j2SjY5qpj603VP9P0BIZBhyqJgeYwf0NZXqnSbpb1YLgSeHjKiIBtRyd+fTvSgtbmyj8MySfZJcOqPsQwznbtz88jy2c2QPkCiZVwtiws57E0Hs/EGjuHce5lQD5nf9xVKzv5klRZZWeMncMR+Zq/Yp4Ps9NK0mA+pl7YP3R+IoEKYqhi1xbZDjVK/eEeqW0kcySFADKuSq74I/opVorCc3FjFM4IZl3ztk8ZpUlcpQaa4j38dcjawauZ2xnjvJfvgSnOAef5qt1yOCK4QRYEmn31Hby+f8VFedEvLYFlUTJk7pzjtkftmhwbsa1Ii3qUxufyMmRdLiTwBXnjV/ulgDjyzWnsrkWV1KiW8axsoJAGlicnG47c9qzPT8NfxBuAc7dsb0ZuBcxMZFTxM/8Tk96z+WzAUsv4eNSpZppDcB0wkjRqw2cAZXPHYiuM+GDtIDglWL4+I32+HrQK26hfldP2SduxyP3pzeNcPlreXxPKQjg4HnwdI+nxrHr24j9G/MMyQR3aAuqurANgjIPlt8hQ1fZ4LcsyzYgJ3i0528s1djlkhkcOzPpVRkb9h+uafBIXIYGUFRpYMApYf8iAPPy9audxvKg0doGvfZ0CTNtMQAMsrjJ+RoObK7YkpAzxHC7AnJ75/CtNbXiXHUbtIzqMJw22N8kfXap2Fy7HRKoXIIwu4GRtufLNXyF2FEymNUQ6lG8zEcDN7siMpiIRYge2QMfLv8DTJba9LN4NtGEB0gk5Px3or1u7WBIigXXKGBKjy53+P5U2znL2fjMmrAOAnc+Qyf1oaXCjfaM0obcAX/ALgezW4WTSys7DlMZ9DWh6QE3uYZZ8sSsiu2QW9RjNUYOtgSiC+tysrZMcMb62YeuNh8Scc70UTCzOwGlT39ST+9UQFbkch6JG4lLrpllkilFuHMZ3IG2O2R9aHzW9zIAxTKhcjB9N9ua0cM751YUgSBQQdyP05HnnBqa7itp9BmRn1DAJyCAf1/arraPqEXkUZE0NAUnULf/AFtEz4pwCuOPeznNCatpYPcdSltbcghWYBm4wDycf3eqsqNFK8bjDIxU/EV0FCjYfvOPkLNRPW0O9VlC9Dsljl0sQpGg4yAu/40qDS2txFEkskTLG/3WPelQRAB7hyOzH1NPZXcF/B40BJXOCGGCD5fjWX9pII7fqZMeR4q+Iw9STn8q09tbwdPtfDiASNBlmPfzJNY7rV8t9ftLH/01GlDjkDvSMH3krxOll+3fmO6NGs16dbFdCFgRjnjv8a13T08ctJqICnGMVi+m+IJJXQ4ATBPlk/xT4+s31tqFvcsELZAIU/TVnA/nzqZ/uj8Y04b9mb6VJEt3FsFEm2CwyOfiPXvSt4nR8MuUVANfBY99vpWJtPa7qUCMJZYbk8/5iAEfDTircPtZe3BSJ0gQykICqEEE7ZBLHes1b3BcOxoLqBjIsqeOSSA2GUHtkHt6VLBZxLF4ULSgFgxMrGTI2BXLZ2IHHbY0kR/BwCQezFcjNWILYJM0+nT7gQnfOxJ7/GoQbhBgLobLH1fqzSAhWuCo9feb+KMoVVnCd9wCfxI/juPkL9mi9xYy3DQoJJJncldwcnnzHw8gKKpbtqJzIc8j+/pUexxvCeeKgP2itA32OOMAffxj5ZqWytinSjGpLuoYjT/ALiDnAzjfGO/aovai5Md5aJG4ONSyqpyVB04z5bcVXh9p7WBVi8K4eRThgqhs/8Ayz59qYSQoXqGk0hgN/fuT2dhO1y1zOZol+6IyyjV6kLnPzPnsKuwo2icsCC7E8cAe6D9AD86r2ftBY30RIkEcoJ/y5TgkeYxzx8fMV2wvLe+tzbwTsJVGCGI8QYxuR37b+vnVRwTFkixJpzPiIxSHVJMNRMQbSByDjG2Mc74PnUt5N9juYtSGQvxlsDtuPPbFOtoWEkhfASRhjyBAAz+HpUXV3HjWsZI1oGOnyzj9j9KWxIBMYtEgSPoEerqV9My+GykroznGSTjPpih1nbp1PrMmn/pF2lbPdc8fPNaQQQ2Ul3d5IWQB3AAwNIOSPjVPolpJbdLLoF8eYahq4G3u5x9fnWr5OWH6TD8X2qerMuXNtb9SgCShmRXPBI3GQf1pVH0i0msrQxTurtrJGkkgDbz9c0qSWKmlO0eEDi2G8wl3fXN4QbiZnA4HAHrgbVWJpU0106A4mbcxK7LnB5GD6ipWjWCIMwJzxt+9RxKWlUCp+oye6qbZG1ZMm71HrssHh9cuFTHwrvUGxEiDG5yafZqGuQByar37h5zj7oOBmkHYmMHUr+7scDPnTgMHUANzv501Rsx9KUZyQPWq1LXLPuyOWZAT60omTUdCBTxsBTI92I7ZxXVH+afiahG1yFiTvCi3jLDGkfu6Bj49zQ15ZBIXI95jnjz32qccUpVT/KMWdIbG/YjmnnEEAruUOdsgo8CKAOyBipClh8Diqdy5e4kLf8AIj9KL28ZFlqEgKLtoIHOec0Iddd2ycapCPxqrfbUoPuuE7X2k6rbaFNwZVXfTMobVnfdvvd/Oi1p1S66oZLmZY0aKJkURggAYyOSd9/wrMXo03jgDAGAAPgKMdLkMdlhdtYIO/xFAISSo/m8jOAoY/zabe26naXPTkFzcIGddEiuQCTwdhwD50P6v1uZLrw7KZRGo3ZQraj8d9v5rP12tS4FBuYm8hytTR9D6tPcXEkd3KrDRqUkBcb/AM/hSrN532pUG8cMbG0snksoo7wdmmk1MbaT0pptnHJX61c5U9zRob1GRSmOYEHBA74qOR9TZJz602QFZihwT3p5hPnWYMNZJjCp0gCctyQ7H0qlLln9TvV1UKll4OMg1TlUiXTjO1KYjeXA4iAxGfhXIB75PkKlmj0QZzntsKks7cPCXL6STxioCtiSjRjIF70o1Pin4mpbSIyZXOnA8s1Lawq926FjhSdwDk74ohloA+4CDZM5io1ADr8TkUW+yWwUZ8XOOdQ/aqQtl/xPw8P4RIHOTx8Kc+VGqotUYXcdBIRbSrpCxjYY74PNULAeJdl2G4y3zqzaQNPFOi5BCHBHc42FP6DaifxWYsNOAMeuf2pYcAi5YoSDUpXQzfvnfOPyozGFRQqjAFC+oQaervEmWOVAzzkgVp06dADuXPoSKumVVZie4rJiZlAHUHg0qKCytxzHn4sf3qQW1sP+7IT/AOp/3pv1KRP0rwPnJzSox9mtScfZkB8tT/vSqfVJD9K/sQTjbft8ajOACQOPOiRXAyVA+AqJLUSzSh5TCmkaWRiDzuMD4Dmufc6IED3EM0VwjvE6rNjRsMnGM7Z2qUgackH6UUVZQQ9xL4sgAAbQBo2wQPLOd/P8KRjVgGIOPhxQBNbyEDqB4LK4uFlkijbQj4bUcZPfA9KkitJVleMxwuOCxwceoPOe9EXtS3Esmg/ejDHDjGMHvj+4qSKJIk0RgYHYDA+m/wBc780DfcIoDbmUJLMKQYm0kee4qusErSMbsuWx7pRc5/ajJQHGee1cC6SvOe5xRgBgCKNpL25khQKCxwM7DJ2Hb8hUC/aLKUO6sBwfI/OtSFHJ4peGDv28qlwVBYkMkCyIw0kE5c4Ax5mopotcavGpy2dWrbsR8v7mjKwIq4VAB2A2xSMK7k437Z/CpzCNpRtrNvB0JGGSVDqySNJ3yMf35VdgtYrNfCiGBy24OT8q5BGltkQjSCc4HI+dSMuv3tRDGgL7hNdQJcQSP7Q+IFyokjJOeNhWhAOc1HZaEudE5YLIukt2z58f3n4TuojbB3HIJHIo31BW1xDcGuYOa6SNjXc453owTm+aVdB34pVJJTRPEByT50/ACFgOKVKhDOMgGF7HeuhNJ59aVKjJGkZKg/7s/KmlwFVsc478b0qVCSdZTnTntTAQo28/OlSoSSQe8fIEZpK2QNhzSpVJJ2MaiRXB94jypUqkk7sB32GeaSnUDntSpUZI4e9vj61IGJIyc5FKlUkjwD51wjB5pUqME4CRx+NKlSqST//Z" , "Tran Trinh" , "ACCEPT"));
        requestFriends.add(new RequestFriend("3","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" , "Pham Sy" , "CANCEL"));
        requestFriends.add(new RequestFriend("2","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" , "Do Quyen" , "CANCEL"));

        RequestAdapter requestAdapter = new RequestAdapter(requestFriends);
        RecyclerView temp = view.findViewById(R.id.requestRecyclerView);
        temp.setAdapter(requestAdapter);
    }

    private void initListenerFriend(String sender, String receiver){
            firebaseFirestore
                    .collection(Constants.KEY_COLLECTION_FRIENDS)
                    .whereEqualTo(Constants.KEY_SENDER_ID, sender)
                    .whereEqualTo(Constants.KEY_RECEIVER_ID, receiver)
                    .addSnapshotListener(eventListener);
            firebaseFirestore
                    .collection(Constants.KEY_COLLECTION_FRIENDS)
                    .whereEqualTo(Constants.KEY_SENDER_ID, receiver)
                    .whereEqualTo(Constants.KEY_RECEIVER_ID, sender)
                    .addSnapshotListener(eventListener);

    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {

        if (error != null) {
            return;
        }
        if (value != null) {
            return;
        }
    };


    private void initialData() {

        TextView temp = view.findViewById(R.id.nameTextView);
        temp.setText(mainScreenActivity.getTextName());
        RoundedImageView image = view.findViewById(R.id.imageProfile);
        image.setImageBitmap(mainScreenActivity.getBitmap());
        firebaseFirestore = FirebaseFirestore.getInstance();


//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//                Intent intenProfile = new Intent(getContext(), ProfileActivity.class);
//                User usertemp = new User();
//                usertemp.setName(mainScreenActivity.preferenceManager.getString(Constants.KEY_NAME));
//                usertemp.setUid(mainScreenActivity.preferenceManager.getString(Constants.KEY_USER_ID));
//                usertemp.setProfileImage(mainScreenActivity.preferenceManager.getString(Constants.KEY_IMAGE));
//                usertemp.setPhoneNumber(mainScreenActivity.preferenceManager.getString(Constants.KEY_NUMBER_PHONE));
//                usertemp.setPassword(mainScreenActivity.preferenceManager.getString(Constants.KEY_PASSWORD));
//                usertemp.setEmail(mainScreenActivity.preferenceManager.getString(Constants.KEY_EMAIL));
//                intenProfile.putExtra(Constants.KEY_USER,user);
//                startActivity(intenProfile);
//            }
    }

    private void loading(Boolean isLoading) {
        TashieLoader temp = view.findViewById(R.id.progressBar);
        if (isLoading) {
            temp.setVisibility(View.VISIBLE);
        } else {
            temp.setVisibility(View.INVISIBLE);
        }
    }

    private void showErrorMessage() {
        TextView temp = view.findViewById(R.id.textErrorMessage);
        temp.setText("Not exist");
        temp.setVisibility(View.VISIBLE);
    }


}