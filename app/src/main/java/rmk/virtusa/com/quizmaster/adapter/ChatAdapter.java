package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rmk.virtusa.com.quizmaster.ChatViewFactory;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Chat;
import rmk.virtusa.com.quizmaster.model.Inbox;

import static rmk.virtusa.com.quizmaster.handler.InboxHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.InboxHandler.UPDATED;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {


    public static final int CHAT_MEDIA_AUDIO = 0;
    public static final int CHAT_MEDIA_PHOTO = 1;
    public static final int CHAT_MEDIA_VIDEO = 2;
    public static final int CHAT_MEDIA_FILE = 3;
    public static final int CHAT_MEDIA_DATE = 4;

    private DateFormat dateFormat;
    private Context context;
    private List<Chat> chats;
    private Inbox inbox;

    public ChatAdapter(Context context, List<Chat> chats, Inbox inbox) {
        this.context = context;
        this.chats = chats;
        this.inbox = inbox;
        dateFormat = SimpleDateFormat.getTimeInstance();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);
        if (holder.chatContentContainer.getChildCount() == 0) {
            holder.chatContentContainer.addView(ChatViewFactory.getChatView(holder.chatContentContainer, chat));

            if (chat.getSenderUid().equals(UserHandler.getInstance().getUserUid())) {
            } else {
                //FIXME cache members list
                UserHandler.getInstance().getUser(chat.getSenderUid(), (user, flag) -> {
                    switch (flag) {
                        case UPDATED:
                            holder.chatHeaderTextView.setText(user.getName());
                            break;
                        case FAILED:
                            Toast.makeText(context, "Failed to get members", Toast.LENGTH_LONG).show();
                            break;
                    }
                });

                TypedValue typedValue = new TypedValue();
                if (context.getTheme().resolveAttribute(R.attr.chatOthersBackground, typedValue, true)) {
                    holder.itemView.setBackgroundColor(typedValue.data);
                }
            }

            holder.chatSentTimeTextView.setText(dateFormat.format(chat.getSentTime()));

            holder.itemView.setOnClickListener(view -> {
                //TODO implement for all types of chat
                if (chat.getIsMedia()) {
                    switch (chat.getMediaType()) {
                        case CHAT_MEDIA_AUDIO:
                            Toast.makeText(context, "Play audio", Toast.LENGTH_SHORT).show();
                            break;
                        case CHAT_MEDIA_PHOTO:
                            Toast.makeText(context, "View photo", Toast.LENGTH_SHORT).show();
                            break;
                        case CHAT_MEDIA_VIDEO:
                            Toast.makeText(context, "View photo", Toast.LENGTH_SHORT).show();
                            break;
                        case CHAT_MEDIA_DATE:
                            Toast.makeText(context, "Add to calendar", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            //TODO handle for corrupt messages
                            break;
                    }
                } else {
                    //no-op
                }
            });
        }

        //TODO implement for chat options: forward, delete and reply
        holder.itemView.setOnLongClickListener(view -> {
            Toast.makeText(context, "Long clicked", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chatHeaderTextView)
        TextView chatHeaderTextView;
        @BindView(R.id.chatSentTimeTextView)
        TextView chatSentTimeTextView;
        @BindView(R.id.chatContentContainer)
        LinearLayout chatContentContainer;

        public ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
