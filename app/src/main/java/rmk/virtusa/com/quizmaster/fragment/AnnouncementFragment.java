package rmk.virtusa.com.quizmaster.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.adapter.AnnouncementAdapter;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.Announcement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnnouncementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnouncementFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    @BindView(R.id.announcementRecyclerView)
    RecyclerView announcementRecyclerView;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);
        unbinder = ButterKnife.bind(this, view);
        List<Announcement> announcements = ResourceHandler.getInstance().getAnnouncements();
        announcementRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        announcementRecyclerView.setAdapter(new AnnouncementAdapter(getContext(), announcements));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
