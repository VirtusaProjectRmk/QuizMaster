package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rmk.virtusa.com.quizmaster.R;

/**
 * Created by sriram on 08/07/18.
 */

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder> {

    Context context;
    ArrayList<String> stats;

    public StatisticsAdapter(Context context, ArrayList<String> stats){
        this.context = context;
        this.stats = stats;
    }

    @NonNull
    @Override
    public StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.statistics_item, parent, false);
        return new StatisticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsViewHolder holder, int position) {
        holder.statVal.setText(stats.get(position));
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    class StatisticsViewHolder extends RecyclerView.ViewHolder {
        TextView statVal;
        public StatisticsViewHolder(View itemView) {
            super(itemView);
            statVal = (TextView)itemView;
        }
    }
}
