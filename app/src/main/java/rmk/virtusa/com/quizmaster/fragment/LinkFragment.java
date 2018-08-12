package rmk.virtusa.com.quizmaster.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.model.Link;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LinkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinkFragment extends Fragment implements ProfileAddFragment.GetData<Link>{

    @BindView(R.id.profileAddCaption)
    TextView profileAddCaption;
    @BindView(R.id.profileAddEditText)
    EditText profileAddEditText;
    Unbinder unbinder;

    public LinkFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LinkFragment newInstance() {
        LinkFragment fragment = new LinkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_link, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public Link getData() {
        return new Link(null, profileAddEditText.getText().toString());
    }
}
