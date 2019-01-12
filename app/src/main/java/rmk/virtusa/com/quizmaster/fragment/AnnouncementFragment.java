package rmk.virtusa.com.quizmaster.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.ProfileActivity;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.adapter.AnnouncementAdapter;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Announcement;

public class AnnouncementFragment extends BaseFragment {

    @BindView(R.id.announcementRecyclerView)
    RecyclerView announcementRecyclerView;
    @BindView(R.id.announcementProgressBar)
    ProgressBar announcementProgressBar;

    AnnouncementAdapter adapter;
    Unbinder unbinder;

    List<Announcement> announcements = new ArrayList<>();

    public AnnouncementFragment() {
        // Required empty public constructor
    }


    void load(boolean b) {
        announcementProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
        announcementProgressBar.setIndeterminate(b);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnnouncement(Announcement announcement) {
        load(false);
        if (announcement.getUserId().isEmpty()) { //indicates announcement delete TODO add a more reliable way
            announcements.remove(announcement);
            adapter.notifyDataSetChanged();

        } else {
            announcements.add(announcement);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AnnouncementAdapter(getContext(), announcements);
        announcementRecyclerView.setAdapter(adapter);
        announcementRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        load(true);
        //start to load announcements
        UserHandler.getInstance().listenForAnnouncements();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
