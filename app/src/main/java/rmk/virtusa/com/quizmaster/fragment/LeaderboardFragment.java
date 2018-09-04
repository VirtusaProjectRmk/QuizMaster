package rmk.virtusa.com.quizmaster.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    List<User> ulist = new ArrayList<>();
    UserListAdapter userListAdapter;


    private String mParam1;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * @param param1 Parameter 1.
     * @return A new instance of fragment LeaderboardFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        userListAdapter = new UserListAdapter(getContext(), ulist);
        Collections.sort(ulist, (u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));
        if (mParam1 != null && mParam1.equals("ALL")) {
            listAllBranch();
        } else {
            UserHandler.getInstance().getUsersByBranch(mParam1, (user, flags) -> {
                if (flags == UPDATED) {
                    ulist.add(user);
                    userListAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void listAllBranch() {
        UserHandler.getInstance().getUsers((user, flags) -> {
            if (flags == UPDATED) {
                ulist.add(user);
                userListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        listviewusers.setAdapter(userListAdapter);
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
