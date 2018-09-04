package rmk.virtusa.com.quizmaster.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.ItemOffsetDecoration;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.adapter.AnnouncementAdapter;
import rmk.virtusa.com.quizmaster.handler.AnnouncementHandler;
import rmk.virtusa.com.quizmaster.model.Announcement;

import static rmk.virtusa.com.quizmaster.handler.UserHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnnouncementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnouncementFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    List<Announcement> announcements;
    @BindView(R.id.announcementRecyclerView)
    RecyclerView announcementRecyclerView;
    AnnouncementAdapter adapter;
    Unbinder unbinder;

    // TODO: Rename and change types of parameters
    private String mParam1;


    public AnnouncementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AnnouncementFragment.
     */
    public static AnnouncementFragment newInstance(String param1) {
        AnnouncementFragment fragment = new AnnouncementFragment();
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
        announcements = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AnnouncementHandler.getInstance().setAddedNew(announcement -> {
            if (announcement == null) {
                Toast.makeText(context, "Cannot add announcement, please try again later", Toast.LENGTH_LONG).show();
                return;
            }
            announcements.add(announcement);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter = new AnnouncementAdapter(getContext(), announcements);
        announcementRecyclerView.setAdapter(adapter);
        announcementRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(getContext(), R.dimen.announcement_item_margin);
        announcementRecyclerView.addItemDecoration(itemOffsetDecoration);

        AnnouncementHandler.getInstance().getAnnouncements((announcement, flag) -> {
            switch (flag) {
                case UPDATED:
                    announcements.add(announcement);
                    Handler handler = new Handler(getContext().getMainLooper());
                    adapter.notifyDataSetChanged();
                    break;
                case FAILED:
                    Toast.makeText(AnnouncementFragment.this.getContext(), "Cannot fetch announcements", Toast.LENGTH_LONG).show();
                    break;
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AnnouncementHandler.getInstance().setAddedNew(null);
        unbinder.unbind();
    }
}
