package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.User;

public class ProfileActivity extends AppCompatActivity {

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

    public static final int PICK_IMAGE = 1;

    ResourceHandler resHandler;
    ArrayList<String> stats = new ArrayList<>();


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());

            } catch(FileNotFoundException e) {
                Log.e(TAG, "File not found" + e.getMessage());
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
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

        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        fab.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        fab.setEnabled(false);
        resHandler = ResourceHandler.getInstance();

        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.equals(resHandler.getUser().getName())) {
                    fab.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Name
                String name = profile.getDisplayName();
                this.name.setText(name);
                String email = user.getEmail();
                this.useremail.setText(email);
            }

            User usr = resHandler.getUser(firebaseUid);
            if (usr == null) {
                Log.e(TAG, "User not found");
                Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
                finish();
            } else {
                ansTV.setText(String.valueOf(usr.getQAnsTot()));
                attTV.setText(String.valueOf(usr.getAAttTot()));
                poiTV.setText(String.valueOf(usr.getPointsTot()));
                profileBranch.setText(String.valueOf(usr.getBranch()));
            }
        }
    }


    public void update(View view) {
        User user = resHandler.getUser();
        user.setName(name.getText().toString());
        resHandler.setUser(user);
        fab.setEnabled(false);
    }

}
