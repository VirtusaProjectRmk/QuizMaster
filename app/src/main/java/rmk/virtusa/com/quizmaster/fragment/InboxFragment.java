package rmk.virtusa.com.quizmaster.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.adapter.InboxAdapter;
import rmk.virtusa.com.quizmaster.handler.InboxHandler;
import rmk.virtusa.com.quizmaster.model.Inbox;

import static rmk.virtusa.com.quizmaster.handler.InboxHandler.EMPTY;
import static rmk.virtusa.com.quizmaster.handler.InboxHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.InboxHandler.UPDATED;

public class InboxFragment extends Fragment {


    @BindView(R.id.inboxRecyclerView)
    RecyclerView inboxRecyclerView;
    Unbinder unbinder;
    private List<Inbox> inboxes = new ArrayList<>();


    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InboxAdapter inboxAdapter = new InboxAdapter(getContext(), inboxes);
        inboxRecyclerView.setAdapter(inboxAdapter);
        inboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        InboxHandler.getInstance().getInboxes((inbox, flag) -> {
            switch (flag) {
                case UPDATED:
                    inboxes.add(inbox);
                    inboxAdapter.notifyDataSetChanged();
                    break;
                case FAILED:
                    Toast.makeText(getContext(), "Error fetching inbox, try again later", Toast.LENGTH_LONG).show();
                    break;
                case EMPTY:
                    //TODO create empty inbox indication in UI
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
