package com.example.thanu.adminnapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AdminDelete extends AppCompatActivity {

    private static final String TAG = AdminDelete.class.getSimpleName();
    ;
    Firebase ref1, ref2;
    FirebaseDatabase database;
    String egs, fgs;
    Button mButton;
    TextView sample;
    static int flag = 0;
    DatabaseReference dr;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deletelayout);

        Firebase.setAndroidContext(this);
        ref1 = new Firebase("https://adminnapp.firebaseio.com/");
        ref2 = new Firebase("https://adminnapp.firebaseio.com/");

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //  collectDetails((Map<String, Object>) dataSnapshot.getValue());
                Iterable<DataSnapshot> tests = dataSnapshot.getChildren();
                LinearLayout ll = (LinearLayout) findViewById(R.id.delete);
                ll.setDividerPadding(8);
                for (final DataSnapshot test : tests) {


                    Log.v(TAG, "Values" + test.getValue());

                    Log.d(TAG, "onDataChange: ID of the test" + test.getKey());
                    final String id = test.getKey();

                    Map singleTest = (Map) test.getValue();

                    TextView tv1 = new TextView(AdminDelete.this);
                    tv1.setText("Test Name:  " + singleTest.get("mcqTest"));
                    TextView tv2 = new TextView(AdminDelete.this);
                    tv2.setText("Start time:  " + singleTest.get("fromTime"));
                    TextView tv3 = new TextView(AdminDelete.this);
                    tv3.setText("End time: " + singleTest.get("toTime"));
                    final Button del = new Button(AdminDelete.this);
                    del.setClickable(false);
                    // b.setEnabled(false);
                    del.setText("Delete Test");
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ref1.child(id).removeValue();
                        }
                    });


                    ll.addView(tv1);
                    ll.addView(tv2);
                    ll.addView(tv3);
                    ll.addView(del);


                }


            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // void collectDetails(Map<String,Object>,adminnapp) //


    }
}


  /*  private void collectDetails(Map<String, Object> tests) {





        for (Map.Entry<String, Object> entry : tests.entrySet()) {





            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });


                }


            });


        }

}
}*/




