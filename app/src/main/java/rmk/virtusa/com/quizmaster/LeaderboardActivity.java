package rmk.virtusa.com.quizmaster;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

public class LeaderboardActivity extends AppCompatActivity implements FirestoreList.OnLoadListener<Branch> {

    @BindView(R.id.leaderboardViewPager)
    ViewPager leaderboardViewPager;
    @BindView(R.id.leaderTabLayout)
    TabLayout leaderTabLayout;
    @BindView(R.id.leaderboardEditText)
    EditText leaderboardEditText;
    @BindView(R.id.leaderboardSearchBtn)
    ImageButton leaderboardSearchBtn;

    FirestoreList<Branch> branchFirestoreList;
    LeaderboardPagerAdapter leaderboardPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ButterKnife.bind(this);

        leaderboardSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = leaderboardEditText.getText().toString().toUpperCase();
                getCurrentFragment().performSearch(query);
            }
        });

        branchFirestoreList = new FirestoreList<>(Branch.class, FirebaseFirestore.getInstance().collection("branches"), this);
        leaderboardPagerAdapter = new LeaderboardPagerAdapter(this, branchFirestoreList, getSupportFragmentManager());
        leaderboardViewPager.setAdapter(leaderboardPagerAdapter);
        leaderTabLayout.setupWithViewPager(leaderboardViewPager);
    }

    private LeaderboardFragment getCurrentFragment() {
        LeaderboardFragment leaderboardFragment = null;
        if (leaderboardPagerAdapter != null) {
            leaderboardFragment = (LeaderboardFragment) leaderboardPagerAdapter.getItem(leaderboardViewPager.getCurrentItem());
        }
        return leaderboardFragment;
    }

    @Override
    public void onLoad() {
        leaderboardPagerAdapter.notifyDataSetChanged();
    }
}
