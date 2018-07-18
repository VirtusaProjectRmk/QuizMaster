package rmk.virtusa.com.quizmaster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rmk.virtusa.com.quizmaster.adapter.ChatAdapter;
import rmk.virtusa.com.quizmaster.handler.ChatHandler;
import rmk.virtusa.com.quizmaster.handler.InboxHandler;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.Chat;
import rmk.virtusa.com.quizmaster.model.Inbox;
import rmk.virtusa.com.quizmaster.model.User;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    @BindView(R.id.chatAddButton)
    ImageButton chatAddButton;
    @BindView(R.id.chatMessageEditText)
    EditText chatMessageEditText;
    @BindView(R.id.chatSendBuutton)
    ImageButton chatSendBuutton;
    @BindView(R.id.chatRecyclerView)
    RecyclerView chatRecyclerView;

    private List<Chat> chats = new ArrayList<>();
    private List<User> members = new ArrayList<>();
    private Inbox inbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        String userId = "";
        String inboxId = "";

        inboxId = getIntent().getExtras().getString(getString(R.string.extra_chat_inboxId));
        userId = getIntent().getExtras().getString(getString(R.string.extra_chat_userId));

        if (userId != null) {
            ResourceHandler.getInstance().getUser(userId, (user, flag) -> {
                members.add(user);

            });
        } else if (inboxId != null) {
            InboxHandler.getInstance().getInbox(inboxId, (inbox, flag) -> {
                if (flag == InboxHandler.UPDATED) {
                    this.inbox = inbox;
                    ResourceHandler.getInstance().getUsers(inbox.getUserIds(), (user, flg) -> {
                        if (flg == ResourceHandler.UPDATED) {
                            members.add(user);
                        }
                    });
                } else {
                    Toast.makeText(ChatActivity.this, "Cannot fetch inbox, please try again later", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            ChatHandler.getInstance().getChats(inboxId, (chat, flag) -> {
                chats.add(chat);
            });
        } else {
            Toast.makeText(this, "Selection not valid, try again with different conversation", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chatRecyclerView.setAdapter(new ChatAdapter(this, chats, inbox, members));


    }
}
