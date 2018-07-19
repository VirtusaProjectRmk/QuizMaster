package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.Chat;
import rmk.virtusa.com.quizmaster.model.Inbox;

import static rmk.virtusa.com.quizmaster.handler.InboxHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.InboxHandler.UPDATED;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Chat> chats;
    private Inbox inbox;

    public ChatAdapter(Context context, List<Chat> chats, Inbox inbox) {
        this.context = context;
        this.chats = chats;
        this.inbox = inbox;
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
        //FIXME user cached members list
        ResourceHandler.getInstance().getUser(chat.getSenderUid(), (user, flag) -> {
            switch (flag) {
                case UPDATED:
                    holder.chatHeaderTextView.setText(user.getName());
                    break;
                case FAILED:
                    break;
            }
        });
        holder.chatSentTimeTextView.setText(chat.getSentTime().toString());
        holder.chatContentTextView.setText(chat.getChat());
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
        @BindView(R.id.chatContentTextView)
        TextView chatContentTextView;

        public ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
