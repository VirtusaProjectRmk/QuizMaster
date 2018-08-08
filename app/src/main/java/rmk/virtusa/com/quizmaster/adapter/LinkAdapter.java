package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.model.Link;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.LinkViewHolder> {
    private static final String TAG = "LinkAdapter";
    private Context context;
    private FirestoreList<Link> linkList;

    public LinkAdapter(Context context, FirestoreList<Link> linkList) {
        this.context = context;
        this.linkList = linkList;
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_link, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
        Link link = linkList.get(position);
        holder.linkUrl.setText(link.getUrl());
        holder.linkWebsite.setText(LinkFactory.getWebsite(link.getUrl()));
        holder.linkImageView.setImageResource(LinkFactory.getIcon(link.getUrl()));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return linkList.size();
    }

    private static class LinkFactory {

        @NonNull
        static String getWebsite(String url) {
            String s = "";
            try {
                s = url.split("www.")[1].split(".com")[0];
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return s;
        }

        static int getIcon(String url) {
            if (url.contains("git")) {
                return R.drawable.github_icon;
            } else if (url.contains("linkedin")) {
                return R.drawable.linkedin_icon;
            } else return R.drawable.web_icon;
        }

    }

    public class LinkViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.linkImageView)
        ImageView linkImageView;
        @BindView(R.id.linkWebsite)
        TextView linkWebsite;
        @BindView(R.id.linkUrl)
        TextView linkUrl;

        public LinkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
