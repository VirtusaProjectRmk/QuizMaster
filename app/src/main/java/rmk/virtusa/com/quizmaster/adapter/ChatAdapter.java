package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.ChatActivity;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.model.Chat;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.InboxViewHolder> {
    private Context context;
    private Map<String, Chat> chats;

    public ChatAdapter(Context context, Map<String, Chat> chats) {
        this.context = context;
        this.chats = chats;
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                Log.e("KIK", String.valueOf(itemCount));
            }
        });
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new InboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        final Chat chat = (Chat) chats.values().toArray()[position];
        holder.setIsRecyclable(false);

        holder.inboxName.setText(chat.getName());
        holder.inboxStatus.setText(chat.getStatus());
        Picasso.get()
                .load(chat.getDp())
                .placeholder(R.drawable.default_user)
                .into(holder.inboxImageView);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            //FIXME Update code to select any chatroom
            intent.putExtra(context.getString(R.string.extra_chat_inboxId), chat.getChatId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class InboxViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.inboxName)
        TextView inboxName;
        @BindView(R.id.inboxStatus)
        TextView inboxStatus;
        @BindView(R.id.inboxImageView)
        CircleImageView inboxImageView;

        InboxViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
