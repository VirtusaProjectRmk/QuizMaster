package rmk.virtusa.com.quizmaster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rmk.virtusa.com.quizmaster.adapter.UserListAdapter;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.User;

public class LeaderboardActivity extends AppCompatActivity {

    ListView listviewusers;
    List<User> ulist;
    EditText editText;
    ImageButton imageButton;
    UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        editText = (EditText)findViewById(R.id.editText);
        imageButton = (ImageButton)findViewById(R.id.imageButton);
        listviewusers = (ListView)findViewById(R.id.listviewusers);

        ulist = ResourceHandler.getInstance().getUsers();

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
    protected void onStart(){
        super.onStart();

        Collections.sort(ulist, new Comparator<User>() {
        @Override
                public int compare(User u1,User u2) {
            return Integer.valueOf((int)u2.getPointsTot()).compareTo((int)u1.getPointsTot());
        }

    });

        adapter=new UserListAdapter(LeaderboardActivity.this,ulist);
        listviewusers.setAdapter(adapter);

    }

}
