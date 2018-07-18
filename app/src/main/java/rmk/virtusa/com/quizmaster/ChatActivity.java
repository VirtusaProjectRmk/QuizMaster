package rmk.virtusa.com.quizmaster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
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

import static rmk.virtusa.com.quizmaster.handler.ChatHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.ChatHandler.UPDATED;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    @BindView(R.id.chatAddButton)
    ImageButton chatAddButton;
    @BindView(R.id.chatMessageEditText)
    EditText chatMessageEditText;
    @BindView(R.id.chatSendButton)
    ImageButton chatSendButton;
    @BindView(R.id.chatRecyclerView)
    RecyclerView chatRecyclerView;
    String userId = "";
    String inboxId = "";
    private List<Chat> chats = new ArrayList<>();
    private List<User> members = new ArrayList<>();
    private Inbox inbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        inboxId = getIntent().getExtras().getString(getString(R.string.extra_chat_inboxId));
        userId = getIntent().getExtras().getString(getString(R.string.extra_chat_userId));

        if (userId != null) {
            ResourceHandler.getInstance().getUser(userId, (user, flag) -> {
                members.add(user);

            });
            InboxHandler.getInstance().createInbox(userId, (inbox, flag) -> {
                this.inbox = inbox;
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

        chatSendButton.setOnClickListener(view -> {
            Chat chat = new Chat(FirebaseAuth.getInstance().getCurrentUser().getUid(), false, chatMessageEditText.getText().toString(), new Date(), null);
            if (inboxId == null) {
                return;
            } else if (inboxId.isEmpty()) {
                return;
            }
            ChatHandler.getInstance().addChat(inboxId, chat, (cht, flag) -> {
                switch (flag) {
                    case UPDATED:
                        //TODO UI indicating success probably single tick
                        break;
                    case FAILED:
                        //TODO UI indicating failure probably error icon
                        break;
                }
            });
        });


    }
}
