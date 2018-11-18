package rmk.virtusa.com.quizmaster.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.adapter.AttachmentAdapter;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Announcement;

public class AnnounceFragment extends BottomSheetDialogFragment {
    @BindView(R.id.announceTitle)
    EditText announceTitle;
    @BindView(R.id.annnounceMessage)
    EditText annnounceMessage;
    @BindView(R.id.announceAttachmentRecyclerView)
    RecyclerView announceAttachmentRecyclerView;
    @BindView(R.id.dismiss)
    ImageButton dismiss;
    Unbinder unbinder;
    @BindView(R.id.announceAnonymousPostCheckBox)
    CheckBox announceAnonymousPostCheckBox;
    @BindView(R.id.attachmentAddButton)
    ImageButton attachmentAddButton;

    List<String> attachments = new ArrayList<>();

    Date expiryDate = new Date();
    @BindView(R.id.expiryDatePicker)
    DatePickerTimeline expiryDatePicker;
    @BindView(R.id.expiryCheckBox)
    CheckBox expiryCheckBox;
    FirestoreList<Announcement> announcementFirestoreList;

    public AnnounceFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        announcementFirestoreList = new FirestoreList<>(Announcement.class, FirebaseFirestore.getInstance().collection("announcement"), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View rootView = localInflater.inflate(R.layout.fragment_announce, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expiryCheckBox.setChecked(true);
        expiryCheckBox.setOnCheckedChangeListener((compoundButton, b) -> expiryDatePicker.setEnabled(true));

        announceAttachmentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        AttachmentAdapter attachmentAdapter = new AttachmentAdapter(view.getContext(), attachments);
        announceAttachmentRecyclerView.setAdapter(attachmentAdapter);

        attachmentAddButton.setOnClickListener(v -> {
            EditText editText = new EditText(getContext());
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.attachment_input_title)
                    .setView(editText)
                    .setPositiveButton("Ok", (l, d) -> {
                        String attachmentFile = editText.getText().toString();
                        if (attachmentFile.isEmpty()) {
                            Toast.makeText(view.getContext(), R.string.attachment_input_empty, Toast.LENGTH_LONG).show();
                            return;
                        }
                        attachments.add(attachmentFile);
                        attachmentAdapter.notifyDataSetChanged();
                    })
                    .create().show();
        });

        Date date = new Date();
        expiryDatePicker.setFirstVisibleDate(date.getYear(), date.getMonth(), date.getDate());

        expiryDatePicker.setOnDateSelectedListener((year, month, day, index) -> {
            expiryDate.setYear(year);
            expiryDate.setMonth(month);
            expiryDate.setDate(day);
        });

        dismiss.setOnClickListener(v -> {

            if (!expiryCheckBox.isChecked()) {
                expiryDate = null;
            }

            Announcement announcement = new Announcement(FirebaseAuth.getInstance().getUid(), announceAnonymousPostCheckBox.isChecked(), announceTitle.getText().toString(), annnounceMessage.getText().toString(), attachments, new Date(), expiryDate);
            UserHandler.getInstance().getAnnouncements(null).add(announcement);
            AnnounceFragment.this.dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
