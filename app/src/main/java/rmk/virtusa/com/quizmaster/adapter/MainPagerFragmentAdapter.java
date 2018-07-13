package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.fragment.AnnouncementFragment;
import rmk.virtusa.com.quizmaster.fragment.InboxFragment;

public class MainPagerFragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public MainPagerFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return AnnouncementFragment.newInstance("", "");
        } else {
            return InboxFragment.newInstance("", "");
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.announcement_fragment_title);
            case 1:
                return mContext.getString(R.string.inbox_fragment_title);
            default:
                return null;
        }
    }

}