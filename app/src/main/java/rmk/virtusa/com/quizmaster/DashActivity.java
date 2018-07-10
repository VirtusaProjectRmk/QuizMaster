package com.example.sony.dashboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class DashActivity extends AppCompatActivity {
    private static final String TAG = "DashActivity";

    private ArrayList<String> mTests=new ArrayList<>();
    private ArrayList<String> mDates=new ArrayList<>();
    private ArrayList<String> mTimes= new ArrayList<>();
    private ArrayList<String> mButtons =new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        Log.d(TAG, "onCreate: started.");

        init();
    }

    private void init(){
        mDates.add("From");
        mTests.add("MCQ TEST ");
        mTimes.add("00.00 to 23.59");
        mButtons.add("Start Test");

       /* mDates.add("TUESDAY");
        mTests.add("MCQ TEST 2");
        mTimes.add("00.00 to 23.59");
        mButtons.add("Start Test");


        mDates.add("WEDNESDAY");
        mTests.add("MCQ TEST 3");
        mTimes.add("00.00 to 23.59");
        mButtons.add("Start Test");


        mDates.add("THURSDAY");
        mTests.add("MCQ TEST 4");
        mTimes.add("00.00 to 23.59");
        mButtons.add("Start Test");


        mDates.add("FRIDAY");
        mTests.add("MCQ TEST 5");
        mTimes.add("00.00 to 23.59");
        mButtons.add("Start Test");


*/

initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter= new RecyclerViewAdapter(this,mTests,mDates,mTimes,mButtons);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

}
