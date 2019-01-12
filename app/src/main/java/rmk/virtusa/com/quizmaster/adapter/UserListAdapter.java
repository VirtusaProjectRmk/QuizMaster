package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import rmk.virtusa.com.quizmaster.ProfileActivity;
import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.User;

public class UserListAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> ulist;
    boolean admin = UserHandler.getInstance().getIsAdmin();

    public UserListAdapter(Context context, List<User> ulist)
    {
        super(context, R.layout.listlayout,ulist);
        this.context = context;
        this.ulist = ulist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listViewItem = LayoutInflater.from(context).inflate(R.layout.listlayout,null,true);

        TextView empid = (TextView) listViewItem.findViewById(R.id.empid);
        TextView name = (TextView) listViewItem.findViewById(R.id.profileName);
        TextView score = (TextView) listViewItem.findViewById(R.id.score);

        User users = ulist.get(position);

        empid.setText(users.getId());
        name.setText(users.getName());

        if(admin) {
            score.setText(String.valueOf(users.getPoints()));
        }
        else
            score.setText("");

        listViewItem.setOnClickListener(view ->{
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra(getContext().getString(R.string.extra_profile_id), users.getId());
            getContext().startActivity(intent);
        });

        return  listViewItem;
    }
}
