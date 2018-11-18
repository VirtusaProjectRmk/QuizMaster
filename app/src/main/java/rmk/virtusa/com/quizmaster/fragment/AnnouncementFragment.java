package rmk.virtusa.com.quizmaster.fragment;


import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.ItemOffsetDecoration;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.adapter.AnnouncementAdapter;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Announcement;

public class AnnouncementFragment extends Fragment implements FirestoreList.OnLoadListener<Announcement>, FirestoreList.OnAddListener<Announcement>, FirestoreList.OnRemoveListener<Announcement> {

    FirestoreList<Announcement> announcements;
    @BindView(R.id.announcementRecyclerView)
    RecyclerView announcementRecyclerView;
    AnnouncementAdapter adapter;
    Unbinder unbinder;


    public AnnouncementFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announcements = UserHandler.getInstance().getAnnouncements(this);
        announcements.setOnAddListener(this);
        announcements.setOnRemoveListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(getContext(), R.dimen.announcement_item_margin);
        announcementRecyclerView.addItemDecoration(itemOffsetDecoration);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAdd(Announcement announcement) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRemove(Announcement announcement) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoad() {
        adapter.notifyDataSetChanged();
    }
}
