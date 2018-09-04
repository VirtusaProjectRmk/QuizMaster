package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.adapter.LinkAdapter;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.handler.FirestoreList.OnLoadListener;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Branch;
import rmk.virtusa.com.quizmaster.model.Link;
import rmk.virtusa.com.quizmaster.model.QuizMetadata;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.handler.UserHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class ProfileActivity extends AppActivity {

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
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.useremail)
    TextView useremail;
    @BindView(R.id.profileToolbarLayout)
    CollapsingToolbarLayout profileToolbarLayout;
    @BindView(R.id.profileBranch)
    EditText profileBranch;
    @BindView(R.id.profileAppBarLayout)
    AppBarLayout profileAppBarLayout;
    @BindView(R.id.profileMessageBtn)
    FloatingActionButton profileMessageBtn;
    @BindView(R.id.profileVideoBtn)
    FloatingActionButton profileVideoBtn;
    @BindView(R.id.profileLinkRecyclerView)
    RecyclerView profileLinkRecyclerView;
    @BindView(R.id.userSummaryEditText)
    EditText userSummaryEditText;
    @BindView(R.id.profileProgressBar)
    ProgressBar profileProgressBar;
    @BindView(R.id.quizProgressGraphView)
    GraphView quizProgressGraphView;
    FirestoreList<QuizMetadata> quizMetadataFirestoreList;
    @BindView(R.id.profileLinkAddTextView)
    TextView profileLinkAddTextView;
    private User user = null;
    private FirestoreList<Link> links;
    private LinkAdapter linkAdapter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (user == null) {
            Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Toast.makeText(this, "Image not picked", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                if (inputStream == null) {
                    Toast.makeText(this, "Cannot read the provided image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user == null) return;

                //TODO move FirebaseStorage code to UserHandler
                FirebaseStorage.getInstance().getReference("images/").child(user.getFirebaseUid()).putStream(inputStream)
                        .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    user.setDisplayImage(uri.toString());
                                    UserHandler.getInstance().setUser(user, (user, flag) -> {
                                        switch (flag) {
                                            case UPDATED:
                                                Glide.with(ProfileActivity.this).load(uri.toString()).into(profileImage);
                                                Toast.makeText(ProfileActivity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                                                break;
                                            case FAILED:
                                                Toast.makeText(ProfileActivity.this, "Cannot update profile photo", Toast.LENGTH_SHORT).show();
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

    private void showLoading(boolean isLoading) {
        profileProgressBar.setIndeterminate(isLoading);
        profileProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void initializeUI(User user, String firebaseUid, boolean isEditable) {
        showLoading(false);
        boolean isAdmin = UserHandler.getInstance().getIsAdmin();
        quizMetadataFirestoreList = UserHandler.getInstance().getUserQuizData(user.getFirebaseUid(), () -> {
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

        profileBranch.setText(user.getBranch() == null || user.getBranch().isEmpty() ? "Other" : user.getBranch());
        profileBranch.setEnabled(isAdmin);
        profileBranch.setCompoundDrawablesWithIntrinsicBounds(0, 0, isAdmin ? android.R.drawable.ic_menu_edit : 0, 0);

        name.setEnabled(isEditable);
        name.setText(user.getName() == null || user.getName().isEmpty() ? "No name" : user.getName());
        name.setCompoundDrawablesWithIntrinsicBounds(0, 0, isEditable ? android.R.drawable.ic_menu_edit : 0, 0);
        profilePoints.setText(String.valueOf(user.getPoints()));

        Glide.with(this).load(user.getDisplayImage() == null || user.getDisplayImage().isEmpty() ? R.drawable.default_user : user.getDisplayImage()).into(profileImage);
        profileImage.setEnabled(isEditable);

        profileLinkAddTextView.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        userSummaryEditText.setVisibility((user.getSummary() == null
                || user.getSummary().isEmpty())
                && isEditable ? View.VISIBLE : View.GONE);
        userSummaryEditText.setText(user.getSummary() == null || user.getSummary().isEmpty() ? "" : user.getSummary());
        userSummaryEditText.setEnabled(isEditable);
        userSummaryEditText.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        userSummaryEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_edit, 0);

        profileLinkRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        OnLoadListener<Link> onLoadListener = () -> {
            if (linkAdapter != null) {
                linkAdapter.notifyDataSetChanged();
            }
        };
        links = isEditable ? UserHandler.getInstance().getUserLink(onLoadListener) : UserHandler.getInstance().getUserLink(firebaseUid, onLoadListener);
        linkAdapter = new LinkAdapter(this, links, isEditable);
        profileLinkRecyclerView.setAdapter(linkAdapter);

        fab.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        useremail.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        profileMessageBtn.setVisibility(!isEditable ? View.VISIBLE : View.GONE);
        profileVideoBtn.setVisibility(!isEditable ? View.VISIBLE : View.GONE);


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
            name.addTextChangedListener(textWatcher);
            userSummaryEditText.addTextChangedListener(textWatcher);
            profileLinkAddTextView.setOnClickListener(this::add);

            profileImage.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            });
            fab.setOnClickListener(this::update);
            useremail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        } else {
            profileVideoBtn.setOnClickListener(view -> {
                if (!userCheck()) return;
                Toast.makeText(this, "Feature not implemented", Toast.LENGTH_LONG).show();
            });
            profileMessageBtn.setOnClickListener(view -> {
                if (!userCheck()) return;
                Intent intent = new Intent(this, ChatActivity.class);
                //intent.putExtra(getString(R.string.extra_chat_inboxId), UserHandler.getInstance().sendRequest());
                Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show();
            });
        }
    }


    private boolean userCheck() {
        if (user == null) {
            Toast.makeText(this, "Please wait while the profile loads", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        String firebaseUid = null;

        try {
            firebaseUid = getIntent().getExtras().getString(getString(R.string.extra_profile_firebase_uid));
        } catch (NullPointerException npe) {
            Log.e(TAG, "Fatal error");
        }

        if (firebaseUid == null) {
            finish();
            return;
        }
        if (firebaseUid.isEmpty()) {
            finish();
            return;
        }

        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boolean isEditable = (firebaseUid.equals(UserHandler.getInstance().getUserUid()));

        showLoading(true);

        String finalFirebaseUid = firebaseUid;
        UserHandler.getInstance().getUser(firebaseUid, (user, flags) -> {
            switch (flags) {
                case UPDATED:
                    initializeUI(user, finalFirebaseUid, isEditable);
                    break;
                case FAILED:
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        });
    }

    public void add(View view) {
        //TODO delegate operations to a fragment
        links.add(new Link("", "https://www.github.com/someone"));
        linkAdapter.notifyDataSetChanged();
    }


    public void update(View view) {
        if (user == null) {
            Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading(true);
        fab.setVisibility(View.GONE);
        user.setName(name.getText().toString());
        user.setBranch(profileBranch.getText().toString());
        if (userSummaryEditText.getText() != null) {
            user.setSummary(userSummaryEditText.getText().toString());
        }
        try {
            UserHandler.getInstance().setUser(user, (usr, flags) -> {
                switch (flags) {
                    case UPDATED:
                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("branches");
                        collectionReference
                                .whereEqualTo("name", profileBranch.getText().toString())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (queryDocumentSnapshots.getDocuments().size() > 0){
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
                        Toast.makeText(this, "User updated Successfully", Toast.LENGTH_LONG).show();
                        break;
                    case FAILED:
                        Toast.makeText(this, "User update failed", Toast.LENGTH_LONG).show();
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
