package rmk.virtusa.com.quizmaster;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.handler.UserHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class LeaderboardActivity extends AppActivity {

    ListView listviewusers;
    List<User> ulist;
    EditText editText;
    ImageButton imageButton;
    UserListAdapter adapter;
    SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        editText = (EditText) findViewById(R.id.editText);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        listviewusers = (ListView) findViewById(R.id.listviewusers);
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        ulist = new ArrayList<>();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stext = editText.getText().toString().toUpperCase();
                adapter.getFilter().filter(stext);
                listviewusers.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Collections.sort(ulist, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Integer.compare(u2.getPoints(), u1.getPoints());
            }
        });

        UserHandler.getInstance().getUsers((user, flag) -> {
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

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mySwipeRefreshLayout.setRefreshing(true);
                doUpdate();
            }
        });

        adapter = new UserListAdapter(LeaderboardActivity.this, ulist);
        listviewusers.setAdapter(adapter);
    }

    private void doUpdate() {
        Collections.sort(ulist, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return Integer.compare(u2.getPoints(), u1.getPoints());
            }
        });

        adapter.notifyDataSetChanged();
        mySwipeRefreshLayout.setRefreshing(false);

    }


}
