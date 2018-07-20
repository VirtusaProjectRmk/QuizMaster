package rmk.virtusa.com.quizmaster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rmk.virtusa.com.quizmaster.adapter.UserListAdapter;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.handler.ResourceHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.ResourceHandler.UPDATED;

public class LeaderboardActivity extends AppActivity {

    ListView listviewusers;
    List<User> ulist;
    EditText editText;
    ImageButton imageButton;
    UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        editText = (EditText) findViewById(R.id.editText);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        listviewusers = (ListView) findViewById(R.id.listviewusers);

        ulist = new ArrayList<>();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stext = editText.getText().toString().toUpperCase();
                adapter.getFilter().filter(stext);
                //listviewusers.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Collections.sort(ulist, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Integer.valueOf((int) u2.getPointsTot()).compareTo((int) u1.getPointsTot());
            }

        });

        ResourceHandler.getInstance().getUsers((user, flag) -> {
            switch (flag) {
                case UPDATED:
                    ulist.add(user);
                    break;
                case FAILED:
                    //TODO use better checks
                    Toast.makeText(LeaderboardActivity.this, "Failed to fetch some users", Toast.LENGTH_LONG).show();
                    break;
            }
        });

        adapter = new UserListAdapter(LeaderboardActivity.this, ulist);
        listviewusers.setAdapter(adapter);
    }

}
