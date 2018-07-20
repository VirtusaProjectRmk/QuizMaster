package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.handler.ResourceHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.ResourceHandler.UPDATED;

public class ProfileActivity extends AppActivity {

    public static final int PICK_IMAGE = 1;
    private static String TAG = "ProfileActivity";
    @BindView(R.id.attTV)
    TextView attTV;
    @BindView(R.id.poiTV)
    TextView poiTV;
    @BindView(R.id.ansTV)
    TextView ansTV;
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
    ArrayList<String> stats = new ArrayList<>();

    User user = null;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());

            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found" + e.getMessage());
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Boolean isEditable = false;
        String firebaseUid = "";

        try {
            isEditable = getIntent().getExtras().getBoolean(getString(R.string.extra_profile_editable));
            firebaseUid = getIntent().getExtras().getString(getString(R.string.extra_profile_firebase_uid));
        } catch (NullPointerException npe) {
            Log.e(TAG, "Fatal error");
        }

        ResourceHandler.getInstance().getUser(firebaseUid, (user, flags) -> {
            switch (flags) {
                case UPDATED:
                    ProfileActivity.this.user = user;
                    name.setText(user.getName());
                    useremail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    ansTV.setText(String.valueOf(user.getQAnsTot()));
                    attTV.setText(String.valueOf(user.getAAttTot()));
                    poiTV.setText(String.valueOf(user.getPointsTot()));
                    profileBranch.setText(String.valueOf(user.getBranch()));
                    Glide.with(this).load(user.getDisplayImage()).into(profileImage);
                    break;
                case FAILED:
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        });

        fab.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        name.setEnabled(isEditable);
        profileImage.setEnabled(isEditable);

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
                    if (!charSequence.equals(user.getName())) {
                        fab.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            profileImage.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            });
        }
    }


    public void update(View view) {
        user.setName(name.getText().toString());
        ResourceHandler.getInstance().setUser(user, (usr, flags) -> {
            switch (flags) {
                case UPDATED:
                    Toast.makeText(this, "User updated Successfully", Toast.LENGTH_LONG).show();
                    break;
                case FAILED:
                    Toast.makeText(this, "User update failed", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}
