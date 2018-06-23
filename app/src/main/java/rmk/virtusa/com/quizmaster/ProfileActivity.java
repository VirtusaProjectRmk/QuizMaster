package rmk.virtusa.com.quizmaster;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ProfileActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Name
                String name = profile.getDisplayName();
                TextView t= findViewById(R.id.user);
                t.setText(name);
                String email = user.getEmail();
                TextView t1= findViewById(R.id.useremail);
                t1.setText(email);

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
