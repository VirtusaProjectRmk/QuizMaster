package rmk.virtusa.com.quizmaster;



import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.core.Tag;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import rmk.virtusa.com.quizmaster.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.text.ParseException;

public class DashActivity extends AppActivity  {


    private static final String TAG =DashActivity.class.getSimpleName();
    TextView mTest;
   TextView mStart;
   TextView mTo;
   TextView mEnd;
   TextView sample;
   Button mButton;
   String egs,fgs;
   public Firebase href,gref;
    static int flag=0;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        mTest=findViewById(R.id.test);
        mStart=findViewById(R.id.start);
        mTo=findViewById(R.id.to);
        mEnd=findViewById(R.id.end);
        sample=findViewById(R.id.samp);
        mButton=findViewById(R.id.button);

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent myIntent = new Intent(DashActivity.this, MainActivity1.class);
                    DashActivity.this.startActivity(myIntent);
            }
        });




       /* mButton=(Button)findViewById(R.id.button);
        mTest=(TextView)findViewById(R.id.test);
        mStart=(TextView)findViewById(R.id.start);
        mTo=(TextView)findViewById(R.id.to);
        mEnd=(TextView)findViewById(R.id.end);*/


        mTest.setText("MCQ TEST");
        mTo.setText("To");
        mButton.setText("Start Test");



        Firebase.setAndroidContext(this);
        href= new Firebase("https://quizmaster-89038.firebaseio.com/From");
        href.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                egs=dataSnapshot.getValue(String.class);
                mStart.setText(egs);
                

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        Firebase.setAndroidContext(this);
        gref= new Firebase("https://quizmaster-89038.firebaseio.com/To");
        gref.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                fgs=dataSnapshot.getValue(String.class);
                Log.v(TAG, fgs);
                mEnd.setText(fgs);
                Date currentTime= new Date();
                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");

                String strDate= sdf.format(currentTime.getTime());


                Date d3= null;
                try {
                    d3 = sdf.parse(strDate);


                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, currentTime.toString());


                Date d1= null;
                try {
                    d1 = sdf.parse(String.valueOf(egs));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date d2= null;
                try {
                    d2 = sdf.parse(String.valueOf(fgs));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                try {
                    if (d3.getTime() >= d1.getTime() && d3.getTime() <= d2.getTime() && flag==0) {
                        
                        flag=1;
                        mButton.setClickable(true);
                        sample.setText("enabled");
                        Toast.makeText(getApplicationContext(), "Button Enabled", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onCreate: buttoncalled.");

                    }
                    else
                    {
                    mButton.setClickable(false);
                        
                    }
                } catch (NullPointerException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        String st = egs;
        String et = fgs;



//Comparison Code

      /* Date currentTime= new Date();
       SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");

        String strDate= sdf.format(currentTime.getTime());


            Date d3= new Date();
        try {
            d3 = sdf.parse(strDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, currentTime.toString());


        Date d1= new Date();
        try {
            d1 = sdf.parse(String.valueOf(egs));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d2= new Date();
        try {
            d2 = sdf.parse(String.valueOf(fgs));
        } catch (ParseException e) {
            e.printStackTrace();
        }



        if (d3.getTime() >= d1.getTime() && d3.getTime() <= d2.getTime()) {
                mButton.setClickable(true);
                sample.setText("button enabled");
                Toast.makeText(this,"Button Enabled",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCreate: buttoncalled.");

            }


            Log.v(TAG, d3.toString()); */




               /* mButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent = new Intent(DashActivity.this, QuizActivity.class);
                        startActivity(intent);
                    }
                });*/
          /*  }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/







}

}
