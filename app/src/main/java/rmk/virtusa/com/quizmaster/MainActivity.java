package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rmk.virtusa.com.quizmaster.adapter.MainPagerFragmentAdapter;
import rmk.virtusa.com.quizmaster.handler.ResourceHandler;

public class MainActivity extends AppCompatActivity
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //TODO check from intent to see if we are in admin mode

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mainViewPager.setAdapter(new MainPagerFragmentAdapter(this, getSupportFragmentManager()));
        mainTabLayout.setupWithViewPager(mainViewPager);


        for(int i = 0; i < 2; i++){
            LinearLayout view = (LinearLayout)LayoutInflater.from(MainActivity.this).inflate(R.layout.icon_tab_main, null, false);
            ((TextView)view.findViewById(R.id.tabHeaderText)).setText(getString(i == 0? R.string.announcement_fragment_title : R.string.inbox_fragment_title));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageView)view.findViewById(R.id.tabHeaderBadge)).setImageDrawable(i == 0? null : getDrawable(R.drawable.alpha_icon));
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
            startActivity(new Intent(this, DashActivity.class));
        } else if (id == R.id.nav_leaderboard) {

            Intent myIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
            MainActivity.this.startActivity(myIntent);
        }
        else if (id == R.id.nav_profile) {
            Intent myIntent = new Intent(this, ProfileActivity.class);
            myIntent.putExtra(getString(R.string.extra_profile_editable), true);
            myIntent.putExtra(getString(R.string.extra_profile_firebase_uid), ResourceHandler.getInstance().getUser().getFirebaseUid());
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
