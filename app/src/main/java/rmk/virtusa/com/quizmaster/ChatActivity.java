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
    String inboxId = "";
    private List<Chat> chats = new ArrayList<>();
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        //inboxId = getIntent().getExtras().getString(getString(R.string.extra_chat_inboxId));

        inboxId = "j0bKHLN1x43ag7dFw9ki";

        if (inboxId == null) {
            Toast.makeText(this, "Inbox invalid, please try again later", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (inboxId.isEmpty()) {
            Toast.makeText(ChatActivity.this, "InboxId error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //Get the inbox object with the given inboxId
        InboxHandler.getInstance().getInbox(inboxId, (inbox, flag) -> {
            switch (flag) {
                case UPDATED:
                    chatAdapter = new ChatAdapter(this, chats, inbox);
                    //Get chats with the given inboxId
                    ChatHandler.getInstance().getChats(inboxId, (chat, flg) -> {
                        switch (flg) {
                            case UPDATED:
                                chats.add(chat);
                                chatAdapter.notifyDataSetChanged();
                                break;
                            case FAILED:
                                break;
                        }
                    });
                    chatRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    chatRecyclerView.setAdapter(chatAdapter);
                    break;
                case FAILED:
                    break;
            }
        });

        chatSendButton.setOnClickListener(view -> {
            Chat chat = new Chat(FirebaseAuth.getInstance().getCurrentUser().getUid(), false, chatMessageEditText.getText().toString(), new Date(), null);
            ChatHandler.getInstance().addChat(inboxId, chat, (cht, flg) -> {
                switch (flg) {
                    case UPDATED:
                        chats.add(cht);
                        chatAdapter.notifyDataSetChanged();
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
