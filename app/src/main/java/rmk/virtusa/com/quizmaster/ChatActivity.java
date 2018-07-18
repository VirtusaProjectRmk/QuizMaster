package rmk.virtusa.com.quizmaster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.User;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        List<User> members = new ArrayList<>();

        String userId = "";
        String inboxId = "";

        inboxId = getIntent().getExtras().getString(getString(R.string.extra_chat_inboxId));
        userId = getIntent().getExtras().getString(getString(R.string.extra_chat_userId));

        if(userId != null){
            ResourceHandler.getInstance().getUser(userId, (user, flag)->{
                members.add(user);
            });
        } else if(inboxId != null){
            ResourceHandler.getInstance().getUsers
        }

    }
}
