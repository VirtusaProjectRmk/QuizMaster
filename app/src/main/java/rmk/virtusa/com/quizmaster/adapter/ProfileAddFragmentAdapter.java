package rmk.virtusa.com.quizmaster.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import rmk.virtusa.com.quizmaster.R;
import rmk.virtusa.com.quizmaster.fragment.LinkFragment;

public class ProfileAddFragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public ProfileAddFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LinkFragment.newInstance();
            default:
                return LinkFragment.newInstance();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 1;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.profile_add_link_title);
            default:
                return null;
        }
    }

}