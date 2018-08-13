package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rmk.virtusa.com.quizmaster.ProfileActivity;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.Announcement;

import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    private static final String TAG = "AnnouncementAdapter";
    Context context;
    List<Announcement> announcements;

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
        Announcement announcement = announcements.get(position);
        UserHandler.getInstance().getUser(announcement.getFirebaseUid(), (user, flag) -> {
            switch (flag) {
                case UPDATED:
                    if (user.getDisplayImage() == null) {
                        Glide.with(context).load(R.drawable.default_user).into(holder.announcerImage);
                    } else {
                        Glide.with(context).load(announcement.getAnonymousPost() || user.getDisplayImage().isEmpty() ?
                                R.drawable.default_user :
                                user.getDisplayImage()).into(holder.announcerImage);
                    }
                    holder.announcerImage.setOnClickListener((view -> {
                        if (announcement.getAnonymousPost()) {
                            Toast.makeText(context, "Posted Anonymously only admin users can view the profile", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra(context.getString(R.string.extra_profile_firebase_uid), user.getFirebaseUid());
                            context.startActivity(intent);
                        }
                    }));
                    break;
            }
        });

        holder.announcementTitle.setText(announcement.getTitle());
        holder.announcementMessage.setText(announcement.getMessage());
        List<String> attachments = announcement.getAttachments();
        if (attachments == null) {
            holder.attachmentContainer.setVisibility(View.GONE);
        } else if (attachments.size() == 0) {
            holder.attachmentContainer.setVisibility(View.GONE);
        } else {
            holder.attachementRecyclerView.setAdapter(new AttachmentAdapter(context, attachments));
            holder.attachementRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public class AnnouncementViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.announcerImage)
        CircleImageView announcerImage;
        @BindView(R.id.announcementTitle)
        TextView announcementTitle;
        @BindView(R.id.announcementMessage)
        TextView announcementMessage;
        @BindView(R.id.attachmentContainer)
        LinearLayout attachmentContainer;
        @BindView(R.id.attachementRecyclerView)
        RecyclerView attachementRecyclerView;

        public AnnouncementViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
