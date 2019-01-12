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
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.User;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private Context context;
    private List<User> uList;
    private OnUserClickListener listener;

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    public LeaderboardAdapter(Context context, List<User> uList) {
        this.context = context;
        this.uList = uList;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, viewGroup, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int i) {
        final User user = uList.get(i);
        holder.bind(user, i);
    }

    @Override
    public int getItemCount() {
        return uList.size();
    }

    class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView userScore;
        TextView userRank;

        LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userScore = itemView.findViewById(R.id.userScore);
            userRank = itemView.findViewById(R.id.userRank);
        }

        void bind(final User user, final int position) {
            userName.setText(user.getName());
            userScore.setText(String.valueOf(user.getPoints()));
            userRank.setText(String.valueOf(position + 1));

            if (UserHandler.getInstance().getIsAdmin()) {
                userScore.setText(String.valueOf(user.getPoints()));
            } else
                userScore.setText("");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClickListener(user);
                }
            });
        }
    }

    public interface OnUserClickListener {
        void onUserClickListener(User user);
    }
}
