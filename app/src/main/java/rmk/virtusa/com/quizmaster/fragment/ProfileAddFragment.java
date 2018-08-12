package rmk.virtusa.com.quizmaster.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.adapter.ProfileAddFragmentAdapter;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Link;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileAddFragment extends DialogFragment {

    @BindView(R.id.profileAddViewPager)
    ViewPager profileAddViewPager;
    @BindView(R.id.detailAddButton)
    Button detailAddButton;
    Unbinder unbinder;
    @BindView(R.id.profileAddTabLayout)
    TabLayout profileAddTabLayout;
    private OnFragmentInteractionListener mListener;

    public ProfileAddFragment() {
        // Required empty public constructor
    }

    public static ProfileAddFragment newInstance() {
        ProfileAddFragment fragment = new ProfileAddFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile_add, container, false);
        unbinder = ButterKnife.bind(this, view);
        ProfileAddFragmentAdapter profileAddFragmentAdapter = (new ProfileAddFragmentAdapter(getContext(), getChildFragmentManager()));
        profileAddViewPager.setAdapter(profileAddFragmentAdapter);
        profileAddTabLayout.setupWithViewPager(profileAddViewPager);
        detailAddButton.setOnClickListener(view1 -> {
            switch (profileAddTabLayout.getSelectedTabPosition()) {
                case 0:
                    UserHandler.getInstance().getUserLink(null).
                            add((new Link(null, "https://www.github.com/someone")), (link, didUpdate) -> {
                            });
                    break;
            }
            mListener.onFragmentInteraction(null);
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface GetData<T> {
        T getData();
    }
}
