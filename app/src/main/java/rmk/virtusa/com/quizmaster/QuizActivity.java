package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;

import rmk.virtusa.com.quizmaster.handler.ResourceHandler;

public class QuizActivity extends AppCompatActivity {

    private TextView mQuestion, quesNum, timer;
    public Firebase mQuestionRef, mChoice1Ref, mChoice2Ref, mChoice3Ref, mChoice4Ref, mAnswerRef;
    public Button nextButton;
    public RadioButton mRadio1, mRadio2, mRadio3, mRadio4;
    public RadioGroup rg;
    public String mAnswer;
    public int questionCounter = 1;
    public int score = 0;
    public Integer mQuestionNumber[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    ArrayList<Integer> list;
    public long timeLeftInMillis = 1200000;
    public Boolean isClicked = false;
    int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        list = new ArrayList<>(Arrays.asList(mQuestionNumber));
        Collections.shuffle(list);
        list.toArray(mQuestionNumber);

        mRadio1 = findViewById(R.id.choice1);
        mRadio2 = findViewById(R.id.choice2);
        mRadio3 = findViewById(R.id.choice3);
        mRadio4 = findViewById(R.id.choice4);
        rg = findViewById(R.id.radio);

        timer = findViewById(R.id.text_view_countdown);
        quesNum = findViewById(R.id.question_num);
        mQuestion = findViewById(R.id.question);
        nextButton = findViewById(R.id.next);


        updateQuestion();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionCounter <= 12) {
                    if (rg.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(QuizActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                    } else {
                        checkAnswer();
                    }
                }
            }
        });
    }

    public void onBackPressed() {

        Intent intent = new Intent(QuizActivity.this, FinishActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("type", FinishActivity.BACK_PRESSED);
        startActivity(intent);

    }

    protected void onUserLeaveHint() {
        
        if(!isClicked) {
            Intent intent = new Intent(QuizActivity.this, FinishActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("type", FinishActivity.BACK_PRESSED);
            startActivity(intent);
            super.onUserLeaveHint();
        }
    }

    CountDownTimer countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            String timeToShow = String.format(Locale.getDefault(), "%02d:%02d", (int) (timeLeftInMillis / 1000) / 60, (int) (timeLeftInMillis / 1000) % 60);
            timer.setText(timeToShow);
            if (timeLeftInMillis < 10000) {
                timer.setTextColor(Color.RED);
            }
            timeLeftInMillis = millisUntilFinished;
        }

        @Override
        public void onFinish() {
            checkAnswer();
            Intent intent = new Intent(QuizActivity.this, FinishActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("type", FinishActivity.TIME_UP);
            startActivity(intent);
        }
    }.start();

    public void updateQuestion() {

        rg.clearCheck();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String url = "url";
        switch (day) {
            case Calendar.MONDAY:
                url = "https://quizmaster-89038.firebaseio.com/0/questions1/";
                break;
            case Calendar.TUESDAY:
                url = "https://quizmaster-89038.firebaseio.com/1/questions2/";
                break;
            case Calendar.WEDNESDAY:
                url = "https://quizmaster-89038.firebaseio.com/2/questions3/";
                break;
            case Calendar.THURSDAY:
                url = "https://quizmaster-89038.firebaseio.com/3/questions4/";
                break;
            case Calendar.FRIDAY:
                url = "https://quizmaster-89038.firebaseio.com/4/questions5/";
                break;
        }
        Firebase.setAndroidContext(this);
        mQuestionRef = new Firebase(url + mQuestionNumber[c] + "/question");
        mChoice1Ref = new Firebase(url + mQuestionNumber[c] + "/choice1");
        mChoice2Ref = new Firebase(url + mQuestionNumber[c] + "/choice2");
        mChoice3Ref = new Firebase(url + mQuestionNumber[c] + "/choice3");
        mChoice4Ref = new Firebase(url + mQuestionNumber[c] + "/choice4");
        mAnswerRef = new Firebase(url + mQuestionNumber[c] + "/answer");

        mQuestionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String questions = dataSnapshot.getValue(String.class);
                mQuestion.setText(questions);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        mChoice1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String choice = dataSnapshot.getValue(String.class);
                mRadio1.setText(choice);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        mChoice2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String choice = dataSnapshot.getValue(String.class);
                mRadio2.setText(choice);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mChoice3Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String choice = dataSnapshot.getValue(String.class);
                mRadio3.setText(choice);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        mChoice4Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String choice = dataSnapshot.getValue(String.class);
                mRadio4.setText(choice);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        mAnswerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAnswer = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        if (questionCounter == 12) {
            String ans = "Submit";
            nextButton.setText(ans);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClicked = true;
                    if (rg.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(QuizActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                    } else {
                        checkAnswer();
                        Intent intent = new Intent(QuizActivity.this, FinishActivity.class);
                        intent.putExtra("score", score);
                        intent.putExtra("type", FinishActivity.QUIZ_COMPLETED);
                        startActivity(intent);
                    }
                }
            });
        }

        String ans = "Question: " + questionCounter + "/" + 12;
        quesNum.setText(ans);
        questionCounter++;
        c++;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void checkAnswer() {

        int selected = rg.getCheckedRadioButtonId();
        RadioButton rbSelected = findViewById(selected);
        if (rbSelected.getText().equals(mAnswer)) {
            score++;
            ResourceHandler.getInstance().incQAnsTot();
            ResourceHandler.getInstance().incPointsTot(1);
        }
        if (questionCounter <= 12) {
            updateQuestion();
            ResourceHandler.getInstance().incAAttTot();
        }

    }
}
