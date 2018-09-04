package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import rmk.virtusa.com.quizmaster.fragment.LeaderboardFragment;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.model.Branch;

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
        return LeaderboardFragment.newInstance(branches.get(position).getValue());
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