package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class FinishActivity extends AppActivity {

    final static int QUIZ_COMPLETED = 1;
    final static int BACK_PRESSED = 2;
    final static int TIME_UP = 3;
    final static int NETWORK_DOWN = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);


        TextView finishDesTextView = findViewById(R.id.finishDesTextView);
        TextView finishScoreTextView = findViewById(R.id.finishScoreTextView);
        Button finishOkButton = findViewById(R.id.finishOkButton);


        int type = -1;
        int score = 0;
        try {
            score = getIntent().getExtras().getInt("score");
            type = getIntent().getExtras().getInt("type");
        } catch (NullPointerException npe) {
            //TODO report error
        }
        switch (type) {
            case QUIZ_COMPLETED:
                finishDesTextView.setText(getString(R.string.completion));
                break;
            case BACK_PRESSED:
                finishDesTextView.setText(getString(R.string.back));
                break;
            case TIME_UP:
                finishDesTextView.setText(getString(R.string.timeup));
                break;
            case NETWORK_DOWN:
                finishDesTextView.setText(getString(R.string.finishNetworkDown));
                break;
            default:
                finishDesTextView.setText(getString(R.string.quiz_submit_error));
                break;
        }

        finishScoreTextView.setText("You Scored: " + score + " points");

        finishOkButton.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(FinishActivity.this, MainActivity.class));
        });

    }

    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

}