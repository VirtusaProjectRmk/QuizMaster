package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.adapter.LinkAdapter;
import rmk.virtusa.com.quizmaster.fragment.LinkFragment;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Branch;
import rmk.virtusa.com.quizmaster.model.Link;
import rmk.virtusa.com.quizmaster.model.QuizMetadata;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.handler.UserHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class ProfileActivity extends BaseActivity {

    public static final int PICK_IMAGE = 1;
    private static String TAG = "ProfileActivity";
    @BindView(R.id.profilePoints)
    TextView profilePoints;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.profileToolbar)
    Toolbar profileToolbar;
    @BindView(R.id.profileImage)
    CircleImageView profileImage;
    @BindView(R.id.profileName)
    EditText profileName;
    @BindView(R.id.profileBranch)
    EditText profileBranch;
    @BindView(R.id.profileMessageBtn)
    FloatingActionButton profileMessageBtn;
    @BindView(R.id.userSummaryEditText)
    EditText userSummaryEditText;
    @BindView(R.id.profileProgressBar)
    ProgressBar profileProgressBar;
    @BindView(R.id.quizProgressGraphView)
    GraphView quizProgressGraphView;
    FirestoreList<QuizMetadata> quizMetadataFirestoreList;
    private User user = null;
    private LinkAdapter linkAdapter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (user == null) {
            Felix.show(this, "Please try again later");
            return;
        }
        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Felix.show(this, "Image not picked");
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                if (inputStream == null) {
                    Felix.show(this, "Cannot read the provided image");
                    return;
                }
                if (user == null) return;

                //TODO move FirebaseStorage code to UserHandler
                FirebaseStorage.getInstance().getReference("images/").child(user.getId()).putStream(inputStream)
                        .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    user.setDisplayImage(uri.toString());
                                    UserHandler.getInstance().setUser(user, (user, flag) -> {
                                        switch (flag) {
                                            case UPDATED:
                                                Picasso.get().load(uri.toString()).into(profileImage);
                                                Felix.show(ProfileActivity.this, "Profile image updated");
                                                break;
                                            case FAILED:
                                                Felix.show(ProfileActivity.this, "Cannot update profile photo");
                                                break;
                                        }
                                    });
                                }));
            } catch (FileNotFoundException fnfe) {
                Log.e(TAG, fnfe.getMessage());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoad(User user) {
        showLoading(false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.linkFragment, LinkFragment.newInstance(user.getId())).commit();
        boolean isAdmin = UserHandler.getInstance().getIsAdmin();
        boolean isEditable = (user.getId().equals(UserHandler.getUserId()));
        quizMetadataFirestoreList = UserHandler.getInstance().getUserQuizData(user.getId(), () -> {
            quizProgressGraphView.setTitle("Quiz Stats");
            LineGraphSeries<DataPoint> dataPointLineGraphSeries = new LineGraphSeries<>(toDataPoint(quizMetadataFirestoreList));
            quizProgressGraphView.addSeries(dataPointLineGraphSeries);
            quizProgressGraphView.getViewport().setYAxisBoundsManual(true);
            quizProgressGraphView.getViewport().setMinY(0);
            quizProgressGraphView.getViewport().setMaxY(12);
            quizProgressGraphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            quizProgressGraphView.getGridLabelRenderer().setNumHorizontalLabels(3);
            quizProgressGraphView.getViewport().setScrollable(true);
        });
        this.user = user;

        profileBranch.setText(user.getBranchId().isEmpty() ? "Other" : user.getBranchId());
        profileBranch.setEnabled(isAdmin);
        profileBranch.setCompoundDrawablesWithIntrinsicBounds(0, 0, isAdmin ? android.R.drawable.ic_menu_edit : 0, 0);

        profileName.setEnabled(isEditable);
        profileName.setText(user.getName().isEmpty() ? "No profileName" : user.getName());
        profileName.setCompoundDrawablesWithIntrinsicBounds(0, 0, isEditable ? android.R.drawable.ic_menu_edit : 0, 0);
        profilePoints.setText(getString(R.string.points_formatter, String.valueOf(user.getPoints())));

        Picasso.get().load(user.getDisplayImage()).placeholder(R.drawable.default_user).into(profileImage);
        profileImage.setEnabled(isEditable);


        userSummaryEditText.setVisibility((user.getSummary().isEmpty())
                && isEditable ? View.VISIBLE : View.GONE);
        userSummaryEditText.setText(user.getSummary().isEmpty() ? "" : user.getSummary());
        userSummaryEditText.setEnabled(isEditable);
        userSummaryEditText.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        userSummaryEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_edit, 0);

        fab.setVisibility(isEditable ? View.VISIBLE : View.GONE);


        profileMessageBtn.setVisibility(!isEditable ? View.VISIBLE : View.GONE);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                fab.setVisibility(View.VISIBLE);
            }
        };

        if (isAdmin) {
            profileBranch.addTextChangedListener(textWatcher);
        }

        if (isEditable) {
            profileName.addTextChangedListener(textWatcher);
            userSummaryEditText.addTextChangedListener(textWatcher);

            profileImage.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            });
            fab.setOnClickListener(this::update);
        } else {
            profileMessageBtn.setOnClickListener(view -> {
                if (userCheck()) return;
                Intent intent = new Intent(this, ChatActivity.class);
                //intent.putExtra(getString(R.string.extra_chat_inboxId), UserHandler.getInstance().sendRequest());
                Felix.show(this, "Work in progress");
            });
        }
    }

    private void showLoading(boolean isLoading) {
        profileProgressBar.setIndeterminate(isLoading);
        profileProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private boolean userCheck() {
        if (user == null) {
            Felix.show(this, "Please wait while the profile loads");
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        String firebaseUid = "";

        try {
            firebaseUid = getIntent().getExtras().getString(getString(R.string.extra_profile_id));
        } catch (NullPointerException npe) {
            Log.e(TAG, "Fatal error");
        }

        if (firebaseUid.isEmpty()) {
            finish();
            return;
        }

        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showLoading(true);

        UserHandler.getInstance().getUser(firebaseUid);
    }

    public void update(View view) {
        if (user == null) {
            Felix.show(this, "Please try again later");
            return;
        }
        showLoading(true);
        fab.setVisibility(View.GONE);
        user.setName(profileName.getText().toString());
        user.setBranchId(profileBranch.getText().toString());
        if (userSummaryEditText.getText() != null) {
            user.setSummary(userSummaryEditText.getText().toString());
        }
        try {
            UserHandler.getInstance().setUser(user, (usr, flags) -> {
                switch (flags) {
                    case UPDATED:
                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("branches");
                        collectionReference
                                .whereEqualTo("profileName", profileBranch.getText().toString())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                        showLoading(false);
                                    } else {
                                        String brnch = profileBranch.getText().toString();
                                        collectionReference.document(brnch).set(new Branch(brnch, "", null))
                                                .addOnSuccessListener(documentReference -> {
                                                    showLoading(false);
                                                })
                                                .addOnFailureListener(e -> {
                                                    showLoading(false);
                                                });
                                    }
                                    showLoading(false);
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showLoading(false);
                                    }
                                });
                        Felix.show(this, "User updated Successfully");
                        break;
                    case FAILED:
                        Felix.show(this, "User update failed");
                        showLoading(false);
                        break;
                }
            });


        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private DataPoint[] toDataPoint(FirestoreList<QuizMetadata> quizMetadataFirestoreList) {
        //x - date
        //y - questions_answered/questions_attended
        QuizMetadata[] quizMetadatas = new QuizMetadata[quizMetadataFirestoreList.size()];
        quizMetadataFirestoreList.keySet().toArray(quizMetadatas);
        List<DataPoint> dataPointList = new ArrayList<>();
        Arrays.sort(quizMetadatas, (t, t1) -> t.getDateTaken().compareTo(t1.getDateTaken()));

        if (quizMetadatas.length == 0) {
            return new DataPoint[]{new DataPoint(0, 0)};
        }
        quizProgressGraphView.getViewport().setMinX(quizMetadatas[0].getDateTaken().getTime());
        quizProgressGraphView.getViewport().setMaxX(quizMetadatas[quizMetadatas.length - 1].getDateTaken().getTime());
        quizProgressGraphView.getViewport().setXAxisBoundsManual(true);

        for (QuizMetadata quizMetadata : quizMetadatas) {
            dataPointList.add(new DataPoint(quizMetadata.getDateTaken(), quizMetadata.getAnsweredCorrectly()));
        }
        DataPoint[] dataPoints = new DataPoint[quizMetadatas.length];
        return dataPointList.toArray(dataPoints);
    }
}
