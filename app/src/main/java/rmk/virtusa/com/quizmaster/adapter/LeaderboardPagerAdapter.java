package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.fragment.AnnouncementFragment;
import rmk.virtusa.com.quizmaster.fragment.InboxFragment;
import rmk.virtusa.com.quizmaster.fragment.LeaderboardFragment;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.model.Branch;
import rmk.virtusa.com.quizmaster.model.User;

public class LeaderboardPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private FirestoreList<Branch> branches;

    public LeaderboardPagerAdapter(Context context, FirestoreList<Branch> branches, FragmentManager fm) {
        super(fm);
        mContext = context;
        this.branches = branches;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        LeaderboardFragment leaderboardFragment = LeaderboardFragment.newInstance(branches.get(position).getValue());
        return leaderboardFragment;
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return branches.size();
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return branches.get(position).getKey().getName();
    }

}