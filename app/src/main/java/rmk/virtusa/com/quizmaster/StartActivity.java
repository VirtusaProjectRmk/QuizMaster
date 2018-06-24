package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class StartActivity extends AppCompatActivity {

    public Button button;
    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        button = findViewById(R.id.start_quiz);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day == Calendar.SATURDAY || day==Calendar.SUNDAY) {
                    startActivity(new Intent(StartActivity.this,NoTestActivity.class));

                } else {
                    startActivity(new Intent(StartActivity.this, MainActivity1.class));
                }
            }
        });

    }

}