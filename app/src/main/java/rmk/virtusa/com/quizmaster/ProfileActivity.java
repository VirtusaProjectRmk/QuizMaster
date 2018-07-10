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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.User;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.attTV)
    TextView attTV;
    @BindView(R.id.poiTV)
    TextView poiTV;
    @BindView(R.id.ansTV)
    TextView ansTV;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private ProfileActivity profileActivity = this;

    ResourceHandler resHandler;

    ArrayList<String> stats = new ArrayList<>();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        resHandler = ResourceHandler.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Name
                String name = profile.getDisplayName();
                this.name.setText(name);
                String email = user.getEmail();
                this.useremail.setText(email);

            }

            if (resHandler.getUser() != null) {
                ansTV.setText(String.valueOf(resHandler.getUser().getQAnsTot()));
                attTV.setText(String.valueOf(resHandler.getUser().getAAttTot()));
                poiTV.setText(String.valueOf(resHandler.getUser().getPointsTot()));
            }
        }
    }


    public void update(View view) {
        User user = resHandler.getUser();
        user.setName(name.getText().toString());
        resHandler.setUser(user);
    }

}
