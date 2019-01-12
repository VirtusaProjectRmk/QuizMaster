package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import rmk.virtusa.com.quizmaster.adapter.MainPagerFragmentAdapter;
import rmk.virtusa.com.quizmaster.fragment.AnnounceFragment;
import rmk.virtusa.com.quizmaster.fragment.SelectUserFragment;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.User;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, SelectUserFragment.OnUserSelectListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mainTabLayout)
    TabLayout mainTabLayout;
    @BindView(R.id.mainViewPager)
    ViewPager mainViewPager;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.mainFabBtn)
    FloatingActionButton mainFabBtn;
    @BindView(R.id.usersFrameLayout)
    FrameLayout usersFramgeLayout;

    View headerView;
    MainPagerFragmentAdapter pagerAdapter;

    private void onAddClick(View view) {
        AnnounceFragment fragment = new AnnounceFragment();
        fragment.show(getSupportFragmentManager(), "announce_modal_dialog");
    }

    private void onSearchClick(View view) {
        Felix.show(this, "Search clicked");
    }

    private void animateFab(int position) {
        switch (position) {
            case 0:
                if (UserHandler.getInstance().getIsAdmin()) {
                    mainFabBtn.setImageResource(R.drawable.ic_add);
                    mainFabBtn.show();
                    mainFabBtn.setOnClickListener(this::onAddClick);
                } else {
                    mainFabBtn.hide();
                }
                break;
            case 1:
                mainFabBtn.setImageResource(android.R.drawable.ic_menu_search);
                mainFabBtn.show();
                mainFabBtn.setOnClickListener(this::onSearchClick);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUser(User user) {
        ((TextView) headerView.findViewById(R.id.userName)).setText(user.getName());
        ((TextView) headerView.findViewById(R.id.profilePoints)).setText(user.getPoints());
        headerView.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, ProfileActivity.class);
            myIntent.putExtra(getString(R.string.extra_profile_id), UserHandler.getUserId());
            MainActivity.this.startActivity(myIntent);
        });
        if (!user.getDisplayImage().isEmpty()) {
            Picasso.get()
                    .load(user.getDisplayImage())
                    .error(R.drawable.default_user)
                    .into((ImageView) headerView.findViewById(R.id.profileImage));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        headerView = navView.inflateHeaderView(R.layout.profile_header);

        animateFab(0);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.usersFrameLayout, SelectUserFragment.newInstance("")).commit();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        pagerAdapter = new MainPagerFragmentAdapter(this, getSupportFragmentManager());
        mainViewPager.setAdapter(pagerAdapter);
        mainTabLayout.setupWithViewPager(mainViewPager);


        for (int i = 0; i < 2; i++) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.icon_tab_main, null, false);
            ((TextView) view.findViewById(R.id.tabHeaderText)).setText(getString(i == 0 ? R.string.announcement_fragment_title : R.string.chat_fragment_title));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView) view.findViewById(R.id.tabHeaderBadge)).setImageDrawable(i == 0 ? null : getDrawable(R.drawable.alpha_icon));
            }
            mainTabLayout.getTabAt(i).setCustomView(view);
        }

        mainViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                animateFab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        navView.setNavigationItemSelectedListener(this);

        UserHandler.getInstance().getUser();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.no_test_header))
                        .setMessage(getString(R.string.no_test))
                        .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                            dialog.dismiss();
                        }).create().show();
            } else {
                Intent myIntent = new Intent(MainActivity.this, ScheduleActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        } else if (id == R.id.nav_add_test) {
            if (UserHandler.getInstance().getIsAdmin()) {
                Intent intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_remove_test) {
            if (UserHandler.getInstance().getIsAdmin()) {
                Intent intent = new Intent(this, AdminDelete.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_leaderboard) {

            Intent myIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onUserSelect(@NotNull List<User> users) {
        //TODO callback for user select
        if (users.size() == 0) {
            //disable toolbar
        } else {
            //enable toolbar
        }
    }
}
