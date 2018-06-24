package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        TextView txt = findViewById(R.id.score);

        int score = getIntent().getExtras().getInt("arg");

        String ans = "You Scored: "+score+" points";

        txt.setText(ans);

        Button button = findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FinishActivity.this, StartActivity.class));
            }
        });

    }

    public void onBackPressed() {
        startActivity(new Intent(FinishActivity.this,StartActivity.class));
    }

}