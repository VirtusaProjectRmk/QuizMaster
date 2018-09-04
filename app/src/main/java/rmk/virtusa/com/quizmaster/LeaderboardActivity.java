package rmk.virtusa.com.quizmaster;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import rmk.virtusa.com.quizmaster.adapter.LeaderboardPagerAdapter;
import rmk.virtusa.com.quizmaster.fragment.LeaderboardFragment;
import rmk.virtusa.com.quizmaster.handler.FirestoreList;
import rmk.virtusa.com.quizmaster.model.Branch;

public class LeaderboardActivity extends AppActivity {

    EditText editText;
    ImageButton imageButton;
    SwipeRefreshLayout mySwipeRefreshLayout;
    @BindView(R.id.leaderboardViewPager)
    ViewPager leaderboardViewPager;
    @BindView(R.id.leaderTabLayout)
    TabLayout leaderTabLayout;
    FirestoreList<Branch> branchFirestoreList;
    LeaderboardPagerAdapter leaderboardPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ButterKnife.bind(this);
        editText = (EditText) findViewById(R.id.editText);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = editText.getText().toString().toUpperCase();
                getCurrentFragment().performSearch(query);
            }
        });

        branchFirestoreList = new FirestoreList<>(Branch.class, FirebaseFirestore.getInstance().collection("branches"), didLoad -> {
            if (didLoad) {
                leaderboardPagerAdapter = new LeaderboardPagerAdapter(this, branchFirestoreList, getSupportFragmentManager());
                leaderboardViewPager.setAdapter(leaderboardPagerAdapter);
                leaderTabLayout.setupWithViewPager(leaderboardViewPager);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mySwipeRefreshLayout.setRefreshing(true);
                doUpdate();
            }
        });
    }

    private LeaderboardFragment getCurrentFragment() {
        LeaderboardFragment leaderboardFragment = null;
        if (leaderboardPagerAdapter != null) {
            leaderboardFragment = (LeaderboardFragment) leaderboardPagerAdapter.getItem(leaderboardViewPager.getCurrentItem());
        }
        return leaderboardFragment;
    }

    private void doUpdate() {
        mySwipeRefreshLayout.setRefreshing(false);
        getCurrentFragment().update();
    }


}
