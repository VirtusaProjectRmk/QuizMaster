package rmk.virtusa.com.quizmaster.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.adapter.UserListAdapter;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class LeaderboardFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    @BindView(R.id.listviewusers)
    ListView listviewusers;
    Unbinder unbinder;
    UserListAdapter userListAdapter;


    private String mParam1;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    public static LeaderboardFragment newInstance(String param1) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO Remove hard firebase calls. After including in a few fragments the bloat is noticeable
        UserHandler.getInstance().getUsers(mParam1).addOnSuccessListener(it -> {
            List<User> users = it.toObjects(User.class);
            if (users.size() == 0) {
                //TODO handle: display "branch is empty" message
                return;
            }
            Collections.sort(users, (u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));
            userListAdapter = new UserListAdapter(getContext(), users);
            listviewusers.setAdapter(userListAdapter);
        }).addOnFailureListener(e -> {
            //TODO handle: display error message
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void performSearch(String query) {
        if (userListAdapter == null) return;
        userListAdapter.getFilter().filter(query);
        userListAdapter.notifyDataSetChanged();
    }

    public void update() {
        if (userListAdapter == null) return;
        userListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
