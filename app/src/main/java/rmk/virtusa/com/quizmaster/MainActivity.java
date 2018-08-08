package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rmk.virtusa.com.quizmaster.adapter.MainPagerFragmentAdapter;
import rmk.virtusa.com.quizmaster.adapter.UsersListAdapter;
import rmk.virtusa.com.quizmaster.fragment.AnnounceFragment;
import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.handler.UserHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class MainActivity extends AppActivity
        implements NavigationView.OnNavigationItemSelectedListener {


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
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;
    @BindView(R.id.fabSearch)
    FloatingActionButton fabSearch;
    @BindView(R.id.usersListRecyclerView)
    RecyclerView usersListRecyclerView;

    UsersListAdapter usersListAdapter;
    FirebaseAuth auth;
    private List<User> users = new ArrayList<>();

    public MainActivity() {
        super();
        auth = FirebaseAuth.getInstance();
    }

    @OnClick({R.id.fabAdd})
    public void fabAddClick(View view) {
        AnnounceFragment fragment = AnnounceFragment.newInstance();
        fragment.show(getFragmentManager(), "Showned");

    }

    private void animateFab(int position) {
        switch (position) {
            case 0:
                fabAdd.show();
                fabSearch.hide();
                break;
            case 1:
                fabSearch.show();
                fabAdd.hide();
                break;

            default:
                fabAdd.show();
                fabSearch.hide();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        usersListRecyclerView.setAdapter(usersListAdapter);
        usersListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //TODO check from intent to see if we are in admin mode

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        usersListAdapter = new UsersListAdapter(this, users);
        UserHandler.getInstance().getUsers(((user, flag) -> {
            switch (flag) {
                case UPDATED:
                    if (auth.getCurrentUser() == null)
                        return;
                    if (user.getFirebaseUid() == null) return;
                    if (user.getFirebaseUid().equals(auth.getCurrentUser().getUid()))
                        return;
                    users.add(user);
                    usersListAdapter.notifyDataSetChanged();
                    break;
                case FAILED:
                    break;
            }
        }));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mainViewPager.setAdapter(new MainPagerFragmentAdapter(this, getSupportFragmentManager()));
        mainTabLayout.setupWithViewPager(mainViewPager);


        for (int i = 0; i < 2; i++) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.icon_tab_main, null, false);
            ((TextView) view.findViewById(R.id.tabHeaderText)).setText(getString(i == 0 ? R.string.announcement_fragment_title : R.string.inbox_fragment_title));
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
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                Intent myIntent = new Intent(MainActivity.this, NoTestActivity.class);
                MainActivity.this.startActivity(myIntent);
            } else {
                Intent myIntent = new Intent(MainActivity.this, DashActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        } else if (id == R.id.nav_leaderboard) {

            Intent myIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_profile) {
            Intent myIntent = new Intent(this, ProfileActivity.class);
            myIntent.putExtra(getString(R.string.extra_profile_firebase_uid), FirebaseAuth.getInstance().getCurrentUser().getUid());
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
