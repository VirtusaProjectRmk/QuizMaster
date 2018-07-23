package rmk.virtusa.com.quizmaster.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.handler.AnnouncementHandler;
import rmk.virtusa.com.quizmaster.model.Announcement;

import static rmk.virtusa.com.quizmaster.handler.UserHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class AnnounceFragment extends DialogFragment {
    @BindView(R.id.announceTitle)
    EditText announceTitle;
    @BindView(R.id.annnounceMessage)
    EditText annnounceMessage;
    @BindView(R.id.announceAttachmentRecyclerView)
    RecyclerView announceAttachmentRecyclerView;
    @BindView(R.id.dismiss)
    Button dismiss;
    Unbinder unbinder;
    @BindView(R.id.announceAnonymousPostCheckBox)
    CheckBox announceAnonymousPostCheckBox;

    public AnnounceFragment() {
    }

    public static AnnounceFragment newInstance() {
        Bundle args = new Bundle();
        AnnounceFragment fragment = new AnnounceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_announce, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        dismiss.setOnClickListener(v -> {
            //TODO get input from field
            Date expiry = new Date();
            expiry.setDate(26);
            expiry.setMonth(7);
            expiry.setYear(2018);

            /*
            List<String> attachments = new ArrayList<>();
            attachments.add("document_quiz.pdf");
            attachments.add("regulation_change.docx");
            */

            Announcement announcement = new Announcement(FirebaseAuth.getInstance().getUid(), announceAnonymousPostCheckBox.isChecked(), announceTitle.getText().toString(), annnounceMessage.getText().toString(), null, new Date(), expiry);

            AnnouncementHandler.getInstance().addAnnouncement(announcement, (ann, flag) -> {
                switch (flag) {
                    case UPDATED:
                        //TODO change from toast to "Updating UI in dialog"
                        Toast.makeText(getActivity(), "Announcement updated succesfully", Toast.LENGTH_LONG).show();
                        dismiss();
                        break;
                    case FAILED:
                        Toast.makeText(getActivity(), "Cannot add announcement", Toast.LENGTH_LONG).show();
                        dismiss();
                        break;
                }
            });
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
