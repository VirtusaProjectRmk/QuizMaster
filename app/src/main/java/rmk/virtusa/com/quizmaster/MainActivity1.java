package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Calendar;
public class MainActivity1 extends AppActivity {

    Button startQuizButton;

    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        startQuizButton=findViewById(R.id.start_quiz);
        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		Intent intent = new Intent(MainActivity1.this, QuizActivity.class);
		startActivity(intent);
            }
        });
	/*if((BackActivity.s == 1) || (FinishActivity.s == 1) || (TimeUpActivity.s == 1)) {
		startQuizButton.setVisibility(View.INVISIBLE);
		startQuizButton.setEnabled(false);
	}*/
    }

}
