package rmk.virtusa.com.quizmaster.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.ProfileActivity;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.adapter.LeaderboardAdapter;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.User;

public class LeaderboardFragment extends Fragment {
    private static final String ARG_BID = "bid";
    @BindView(R.id.leaderboardRecyclerView)
    RecyclerView leaderboardUserRecyclerView;
    Unbinder unbinder;
    LeaderboardAdapter leaderboardAdapter;


    private String bid;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    public static LeaderboardFragment newInstance(String bid) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BID, bid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bid = getArguments().getString(ARG_BID);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO Remove hard firebase calls. After including in a few fragments the bloat is noticeable
        UserHandler.getInstance().getUsers(bid).addOnSuccessListener(it -> {
            List<User> users = it.toObjects(User.class);
            if (users.size() == 0) {
                //TODO handle: display "branch is empty" message
                return;
            }
            Collections.sort(users, (u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));
            leaderboardAdapter = new LeaderboardAdapter(getContext(), users);
            leaderboardAdapter.setOnUserClickListener(user -> {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra(getString(R.string.extra_profile_id), user.getId());
                startActivity(intent);
            });
            leaderboardUserRecyclerView.setAdapter(leaderboardAdapter);
            leaderboardUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
        }).addOnFailureListener(e -> {
            //TODO handle: display error message
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void performSearch(String query) {
        if (leaderboardAdapter == null) return;
//        leaderboardAdapter.getFilter().filter(query);
        leaderboardAdapter.notifyDataSetChanged();
    }

    public void update() {
        if (leaderboardAdapter == null) return;
        leaderboardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
