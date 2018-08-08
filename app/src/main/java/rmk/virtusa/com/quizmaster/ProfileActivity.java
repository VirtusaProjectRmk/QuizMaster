package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.adapter.LinkAdapter;
import rmk.virtusa.com.quizmaster.fragment.ProfileAddFragment;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Link;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.handler.UserHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class ProfileActivity extends AppActivity implements ProfileAddFragment.OnFragmentInteractionListener {

    public static final int PICK_IMAGE = 1;
    private static String TAG = "ProfileActivity";
    private final FirestoreList<Link> links = UserHandler.getInstance().getLinkList();
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
    TextView profileBranch;
    @BindView(R.id.profileAppBarLayout)
    AppBarLayout profileAppBarLayout;
    @BindView(R.id.profileMessageBtn)
    Button profileMessageBtn;
    @BindView(R.id.profileLinkRecyclerView)
    RecyclerView profileLinkRecyclerView;
    private ArrayList<String> stats = new ArrayList<>();
    private User user = null;
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

    public void changeButtonState(boolean isSave) {
        fab.setImageResource(isSave ? R.drawable.tick_icon : R.drawable.ic_add);
        fab.setOnClickListener(isSave ? this::update : this::add);
    }


    private void initializeUI(boolean isEditable) {
        fab.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        changeButtonState(false);
        useremail.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        name.setEnabled(isEditable);
        profileImage.setEnabled(isEditable);

        profileMessageBtn.setVisibility(!isEditable ? View.VISIBLE : View.GONE);

        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (isEditable) {
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!editable.toString().equals(user.getName())) {
                        changeButtonState(true);
                    }
                }
            });
            profileImage.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            });
        } else {
            profileMessageBtn.setOnClickListener(view -> {
                if (user == null) {
                    Toast.makeText(this, "Please wait while the profile loads", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, ChatActivity.class);
                //intent.putExtra(getString(R.string.extra_chat_inboxId), UserHandler.getInstance().sendRequest());
                Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show();
            });
        }
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

        boolean isEditable = (firebaseUid.equals(UserHandler.getInstance().getUserUid()));

        //FIXME add editable for links
        profileLinkRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        linkAdapter = new LinkAdapter(this, links);
        profileLinkRecyclerView.setAdapter(linkAdapter);

        UserHandler.getInstance().getUser(firebaseUid, (user, flags) -> {
            switch (flags) {
                case UPDATED:
                    this.user = user;
                    name.setText(user.getName());
                    useremail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    profilePoints.setText(String.valueOf(user.getPoints()));
                    profileBranch.setText(String.valueOf(user.getBranch()));
                    Glide.with(this).load(user.getDisplayImage()).into(profileImage);
                    break;
                case FAILED:
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        });
        initializeUI(isEditable);
    }

    public void add(View view) {
        //TODO delegate operations to a fragment
        ProfileAddFragment profileAddFragment = ProfileAddFragment.newInstance();
        profileAddFragment.show(getSupportFragmentManager(), "ProfileFragmentAdd");
    }


    public void update(View view) {
        if (user == null) {
            Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
            return;
        }
        user.setName(name.getText().toString());
        try {
            UserHandler.getInstance().setUser(user, (usr, flags) -> {
                switch (flags) {
                    case UPDATED:
                        changeButtonState(false);
                        Toast.makeText(this, "User updated Successfully", Toast.LENGTH_LONG).show();
                        break;
                    case FAILED:
                        Toast.makeText(this, "User update failed", Toast.LENGTH_LONG).show();
                        break;
                }
            });
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        linkAdapter.notifyDataSetChanged();
    }
}
