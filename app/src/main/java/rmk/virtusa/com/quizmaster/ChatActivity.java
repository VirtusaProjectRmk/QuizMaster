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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.adapter.MessageAdapter;
import rmk.virtusa.com.quizmaster.handler.ChatHandler;
import rmk.virtusa.com.quizmaster.model.Chat;
import rmk.virtusa.com.quizmaster.model.Message;

public class ChatActivity extends BaseActivity {

    private static final String TAG = "ChatActivity";
    @BindView(R.id.chatMessageEditText)
    EditText chatMessageEditText;
    @BindView(R.id.chatSendButton)
    ImageView chatSendButton;
    @BindView(R.id.chatRecyclerView)
    RecyclerView chatRecyclerView;
    @BindView(R.id.chatToolbar)
    Toolbar chatToolbar;
    @BindView(R.id.chatToolbarInboxImage)
    CircleImageView chatToolbarInboxImage;
    @BindView(R.id.chatToolbarTitle)
    TextView chatToolbarTitle;
    @BindView(R.id.chatToolbarInboxContainer)
    LinearLayout chatToolbarInboxContainer;
    @BindView(R.id.chatProgressBar)
    ProgressBar chatProgressBar;

    private List<Message> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;
    String chatId;

    @OnClick({R.id.chatToolbarInboxContainer, R.id.chatSendButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chatToolbarInboxContainer:
                Intent intent = new Intent(this, ChatInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.chatSendButton:
                if (messageAdapter == null || chatId == null) return;
                Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        false, 0, chatMessageEditText.getText().toString(), new Date(),
                        null);
                messages.add(message);
                ChatHandler.getInstance().getMessagesCollectionRef(chatId).add(message)
                        .addOnSuccessListener(queryDocument -> {
                            chatMessageEditText.getText().clear();
                        }).addOnFailureListener(e -> {
                    Felix.show(this, "Send failed");
                });
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
            //TODO implement video call
            //            case R.id.chatVideoCallBtn:
            //                Felix.show(this, "Feature not implemented", Toast.LENGTH_LONG);
            //                Intent intent = new Intent(this, CallActivity.class);
            //                startActivity(intent);
            //                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.chatAddBtn:
                Felix.show(this, "Feature not implemented: Creates a group with the selected user");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        load(true);

        try {
            chatId = getIntent().getExtras().getString(getString(R.string.extra_chat_inboxId));
        } catch (NullPointerException npe) {
            Log.e(TAG, "Intent extra chatId not found");
            finish();
        }

        if (chatId == null) {
            Felix.show(this, "Chat invalid, please try again later");
            finish();
            return;
        }
        if (chatId.isEmpty()) {
            Felix.show(ChatActivity.this, "ChatId error");
            finish();
            return;
        }

        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChatHandler.getInstance().getChat(chatId);
    }


    private void load(boolean b) {
        chatProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
        chatProgressBar.setIndeterminate(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessage(Message message) {
        Felix.show(this, message.getChat());
        if (messageAdapter == null) return;
        messages.add(message);
        messageAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChat(Chat chat) {
        if (chat.getChatId().isEmpty()) {
            Felix.show(this, "Cannot find the specified chat");
            finish();
            return;
        }
        load(false);
        chatToolbarTitle.setText(chat.getName());

        if (chat.getDp().isEmpty()) return;
        Picasso.get()
                .load(chat.getDp())
                .error(R.drawable.default_user)
                .into(chatToolbarInboxImage);
        //Get messages with the given chatId
        ChatHandler.getInstance().getMessages(chatId);
        ChatHandler.getInstance().getMessagesCollectionRef(chatId).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots == null) return;
            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                switch (documentChange.getType()) {
                    case ADDED:
                        Message newMessage = documentChange.getDocument().toObject(Message.class);
                        messages.add(newMessage);
                        messageAdapter.notifyDataSetChanged();
                        break;
                    case REMOVED:
                        Message eMessage = documentChange.getDocument().toObject(Message.class);
                        messages.remove(eMessage);
                        messageAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        messageAdapter = new MessageAdapter(this, chat, messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        chatRecyclerView.setAdapter(messageAdapter);
    }
}
