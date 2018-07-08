package rmk.virtusa.com.quizmaster;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import rmk.virtusa.com.quizmaster.adapter.StatisticsAdapter;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;

public class ProfileActivity extends AppCompatActivity {

    TextView t;
    RecyclerView recyclerView;
    TextView t1;

    ResourceHandler resHandler;

    ArrayList<String> stats = new ArrayList<>();

    void initialize(){
        t = findViewById(R.id.user);
        t1 = findViewById(R.id.useremail);
        recyclerView = findViewById(R.id.userStatisticsRecyclerView);
    }

    void addStat(String title, String val){
        stats.add(title + " : " + val);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialize();

        resHandler = ResourceHandler.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Name
                String name = profile.getDisplayName();
                t.setText(name);
                String email = user.getEmail();
                t1.setText(email);

            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new StatisticsAdapter(this, stats));
            if(resHandler.getUser() != null){
                addStat(getString(R.string.questions_total_attended), String.valueOf(resHandler.getUser().getAAttTot()));
                addStat(getString(R.string.questions_total_answered), String.valueOf(resHandler.getUser().getQAnsTot()));
                addStat(getString(R.string.total_points), String.valueOf(resHandler.getUser().getPointsTot()));
            }
        }
    }


    public void update(View view) {
       final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       EditText t1= findViewById(R.id.name);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(t1.getText().toString().trim())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Profile updated", Toast.LENGTH_LONG).show();
                            for (UserInfo profile : user.getProviderData()) {
                                // Name
                                String name = profile.getDisplayName();
                                TextView t= findViewById(R.id.user);
                                t.setText(name);
                            }
                        }
                    }
                });
    }
}
