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

public class DashActivity extends AppCompatActivity {

   TextView mTest;
   TextView mStart;
   TextView mTo;
   TextView mEnd;
   Button mButton ;
   String egs,fgs;

   public Firebase href,gref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        mButton=(Button)findViewById(R.id.button);
        mTest=(TextView)findViewById(R.id.test);
        mStart=(TextView)findViewById(R.id.start);
        mTo=(TextView)findViewById(R.id.to);
        mEnd=(TextView)findViewById(R.id.end);


        mTest.setText("MCQ TEST");
        mTo.setText("To");
        mButton.setText("Start Test");



        Firebase.setAndroidContext(this);
        href= new Firebase("https://quizmaster-89038.firebaseio.com/From");
        href.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                String egs=dataSnapshot.getValue(String.class);
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
                String fgs=dataSnapshot.getValue(String.class);
                mEnd.setText(fgs);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });






/*Comparison Code

       Date currentTime= new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("HH:MM");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        String strDate= sdf.format(currentTime.getTime());
        try {
            Date d1=sdf.parse(egs);
            Date d2=sdf.parse(fgs);
            Date d3=sdf.parse(strDate);
            if(d3.getTime()>d1.getTime() && d3.getTime()<d2.getTime())
            {
                mButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent = new Intent(DashActivity.this, QuizActivity.class);
                        startActivity(intent);
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

*/







     mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


             Intent intent = new Intent(DashActivity.this, QuizActivity.class);
              startActivity(intent);
            }
        });
}

}
