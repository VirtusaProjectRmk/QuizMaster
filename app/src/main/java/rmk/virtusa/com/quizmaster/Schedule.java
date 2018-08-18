package com.example.thanu.adminnapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.util.Log;



import java.util.ArrayList;
import java.util.Map;

public class Schedule extends AppCompatActivity{


    private static final String TAG =Schedule.class.getSimpleName(); ;
    Firebase ref1,ref2;
    String egs,fgs;
   Button mButton;
   TextView sample;
   static int flag= 0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        Firebase.setAndroidContext(this);
        ref1= new Firebase("https://adminnapp.firebaseio.com/");

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                collectDetails((Map<String,Object>) dataSnapshot.getValue());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // void collectDetails(Map<String,Object>,adminnapp) //



    }

    private void collectDetails(Map<String, Object> tests) {

        LinearLayout ll= (LinearLayout) findViewById(R.id.info);
        ll.setDividerPadding(8);


        for (Map.Entry<String, Object> entry : tests.entrySet()){


            Map singleTest = (Map) entry.getValue();

            TextView tv1 = new TextView(this);
            tv1.setText("Test Name:  " + singleTest.get("mcqTest"));
            TextView tv2 = new TextView(this);
            tv2.setText("Start time:  "+ singleTest.get("fromTime"));
            TextView tv3 = new TextView(this);
            tv3.setText("End time: "+ singleTest.get("toTime"));
           final Button b = new Button(this);
          /*  b.setClickable(false);
            b.setEnabled(false);
            b.setText("Take Test");*/



            ll.addView(tv1);
            ll.addView(tv2);
            ll.addView(tv3);
         //   ll.addView(b);




            Date currentTime = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            String strDate = sdf.format(currentTime.getTime());


            Date d3 = null;


            try {
                d3 = sdf.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d(TAG, currentTime.toString());
            Date d1= null;

            try {
                d1 = sdf.parse(String.valueOf(singleTest.get("fromTime")));
            } catch(ParseException e) {
                e.printStackTrace();
            }
            Date d2=null;

            try {
                d2 = sdf.parse(String.valueOf(singleTest.get("toTime")));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            try {
                if (d3.getTime() >= d1.getTime() && d3.getTime() <= d2.getTime() ) {




                    b.setText("Take Test");
                    ll.addView(b);


                    //flag = 1;
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            b.setClickable(false);
                            b.setEnabled(false);
                   b.setText("Take Test");
                     b.setVisibility(View.GONE);

                            Intent intent = new Intent(Schedule.this,Schedule.class);
                            Schedule.this.startActivity(intent);
                        }
                    });
                    //  sample.setText("enabled");
                    // Toast.makeText(getApplicationContext(), "Button Enabled", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCreate: buttoncalled.");

                } else {

                    //  Toast.makeText(getApplicationContext(), "Come back later", Toast.LENGTH_SHORT).show();

                    b.setText("Come back later");
                    b.setClickable(false);
                    ll.addView(b);



                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }



            String st = egs;
            String et = fgs;


        }


    }
}