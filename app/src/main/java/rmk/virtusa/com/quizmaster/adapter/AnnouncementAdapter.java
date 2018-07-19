package rmk.virtusa.com.quizmaster.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;
import rmk.virtusa.com.quizmaster.model.Announcement;

import static rmk.virtusa.com.quizmaster.handler.ResourceHandler.UPDATED;

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
        ResourceHandler.getInstance().getUser(announcement.getFirebaseUid(), (user, flag) -> {
            switch (flag) {
                case UPDATED:
                    Glide.with(context).load(announcement.getAnonymousPost() ? R.drawable.default_user : user.getDisplayImage()).into(holder.announcerImage);
                    holder.announcerImage.setOnClickListener((view -> {
                        if (announcement.getAnonymousPost()) {
                            Toast.makeText(context, "Posted Anonymously only admin users can view the profile", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra(context.getString(R.string.extra_profile_firebase_uid), user.getFirebaseUid());
                            intent.putExtra(context.getString(R.string.extra_profile_editable), false);
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

    public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder> {

        Context context;
        List<String> attachments;

        public AttachmentAdapter(Context context, List<String> attachments) {
            this.context = context;
            this.attachments = attachments;
        }

        @NonNull
        @Override
        public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.attachment_item, parent, false);
            return new AttachmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
            String fileUrl = attachments.get(position);
            //TODO Get filename from url
            holder.itemView.setOnClickListener(view -> {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(fileUrl));
                    context.startActivity(i);
                } catch (ActivityNotFoundException anfe) {
                    Log.e(TAG, anfe.getMessage());
                }
            });
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            ;
            holder.attachmentName.setText(fileName);
            Glide.with(context).load(R.drawable.unknown_file_download).into(holder.attachmentImage);
        }

        @Override
        public int getItemCount() {
            return attachments.size();
        }

        public class AttachmentViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.attachmentImage)
            ImageView attachmentImage;
            @BindView(R.id.attachmentName)
            TextView attachmentName;

            public AttachmentViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
