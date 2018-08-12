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

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.ChatActivity;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.model.Inbox;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {
    Context context;
    List<Inbox> inboxes;

    public InboxAdapter(Context context, List<Inbox> inboxes) {
        this.context = context;
        this.inboxes = inboxes;
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
        View view = LayoutInflater.from(context).inflate(R.layout.inbox_item, parent, false);
        return new InboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        Inbox inbox = inboxes.get(position);
        holder.inboxName.setText(inbox.getName());
        holder.inboxStatus.setText(inbox.getStatus());


        if (inbox.getInboxImage() == null) {
            Glide.with(context).load(R.drawable.default_user).into(holder.inboxImageView);
        } else {
            Glide.with(context)
                    .load(inbox.getInboxImage().isEmpty() ? R.drawable.default_user : inbox.getInboxImage())
                    .into(holder.inboxImageView);
        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            //FIXME Update code to select any chatroom
            intent.putExtra(context.getString(R.string.extra_chat_inboxId), "j0bKHLN1x43ag7dFw9ki");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return inboxes.size();
    }

    public class InboxViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.inboxName)
        TextView inboxName;
        @BindView(R.id.inboxStatus)
        TextView inboxStatus;
        @BindView(R.id.inboxImageView)
        CircleImageView inboxImageView;

        public InboxViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
