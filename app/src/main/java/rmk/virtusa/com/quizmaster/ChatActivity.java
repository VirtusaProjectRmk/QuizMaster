package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.adapter.ChatAdapter;
import rmk.virtusa.com.quizmaster.handler.InboxHandler;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Chat;

import static rmk.virtusa.com.quizmaster.handler.InboxHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.InboxHandler.UPDATED;

public class ChatActivity extends AppActivity {

    private static final String TAG = "ChatActivity";
    @BindView(R.id.chatProfileImageView)
    CircleImageView chatProfileImageView;
    @BindView(R.id.chatMessageEditText)
    EditText chatMessageEditText;
    @BindView(R.id.chatSendButton)
    ImageView chatSendButton;
    @BindView(R.id.chatRecyclerView)
    RecyclerView chatRecyclerView;
    String inboxId = "";
    @BindView(R.id.chatToolbar)
    Toolbar chatToolbar;
    @BindView(R.id.chatToolbarInboxImage)
    CircleImageView chatToolbarInboxImage;
    @BindView(R.id.chatToolbarTitle)
    TextView chatToolbarTitle;
    @BindView(R.id.chatToolbarInboxContainer)
    LinearLayout chatToolbarInboxContainer;
    private List<Chat> chats = new ArrayList<>();
    private ChatAdapter chatAdapter;

    @OnClick({R.id.chatToolbarInboxContainer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chatToolbarInboxContainer:
                Intent intent = new Intent(this, InboxInfoActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatVideoCallBtn:
                //TODO implement video call
                Toast.makeText(this, "Feature not implemented", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(this, CallActivity.class);
                //startActivity(intent);
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            inboxId = getIntent().getExtras().getString(getString(R.string.extra_chat_inboxId));
        } catch (NullPointerException npe) {
            Log.e(TAG, "Intent extra inboxId not found");
        }

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

        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserHandler.getInstance().getUser((user, flag) -> {
            Glide.with(this).load(user.getDisplayImage()).into(chatProfileImageView);
        });

        //Get the inbox object with the given inboxId
        InboxHandler.getInstance().getInbox(inboxId, (inbox, flag) -> {
            switch (flag) {
                case UPDATED:
                    chatToolbarTitle.setText(inbox.getName());
                    Glide.with(this).load(inbox.getInboxImage()).into(chatToolbarInboxImage);
                    chatAdapter = new ChatAdapter(this, chats, inbox);
                    //Get chats with the given inboxId
                    InboxHandler.getInstance().getChats(inboxId, (chat, flg) -> {
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
                    Toast.makeText(this, "Cannot find the specified inbox", Toast.LENGTH_LONG).show();
                    finish();
                    return;
            }
        });

        chatSendButton.setOnClickListener(view -> {
            Chat chat = new Chat(FirebaseAuth.getInstance().getCurrentUser().getUid(), false, chatMessageEditText.getText().toString(), new Date(), null);
            InboxHandler.getInstance().addChat(inboxId, chat, (cht, flg) -> {
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
