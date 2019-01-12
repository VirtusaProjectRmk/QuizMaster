package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.ProfileActivity;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Announcement;
import rmk.virtusa.com.quizmaster.model.User;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    private static final String TAG = "AnnouncementAdapter";
    private Context context;
    private List<Announcement> announcements;

    public AnnouncementAdapter(Context context, List<Announcement> announcements) {
        this.context = context;
        this.announcements = announcements;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.announcement_item, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        final Announcement announcement = announcements.get(position);

        //TODO remove this ugly piece of code
        //NOTE data should never be attached like this
        UserHandler.getInstance().usersRef.document(announcement.getUserId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    final User user = documentSnapshot.toObject(User.class);
                    if (user == null || user.getId().isEmpty()) return;
                    holder.announcerImage.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra(context.getString(R.string.extra_profile_id), user.getId());
                        context.startActivity(intent);
                    });
                    if (user.getDisplayImage().isEmpty()) return;
                    Picasso.get()
                            .load(user.getDisplayImage())
                            .error(R.drawable.default_user)
                            .into(holder.announcerImage);
                });

        holder.announcementTitle.setText(announcement.getTitle());
        holder.announcementMessage.setText(announcement.getMessage());
        List<String> attachments = announcement.getAttachments();
        if (attachments.size() == 0) {
            holder.attachementRecyclerView.setVisibility(View.GONE);
        } else {
            holder.attachementRecyclerView.setAdapter(new AttachmentAdapter(context, attachments));
            holder.attachementRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.announcerImage)
        CircleImageView announcerImage;
        @BindView(R.id.announcementTitle)
        TextView announcementTitle;
        @BindView(R.id.announcementMessage)
        TextView announcementMessage;
        @BindView(R.id.attachementRecyclerView)
        RecyclerView attachementRecyclerView;

        AnnouncementViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
