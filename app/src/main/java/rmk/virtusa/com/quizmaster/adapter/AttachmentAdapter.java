package rmk.virtusa.com.quizmaster.adapter;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rmk.virtusa.com.quizmaster.R;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder> {

    private static final String TAG = "AttachmentAdapter";

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
        holder.attachmentName.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    public class AttachmentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.attachmentName)
        TextView attachmentName;

        public AttachmentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}