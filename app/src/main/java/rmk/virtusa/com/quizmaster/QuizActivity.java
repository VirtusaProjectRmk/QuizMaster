package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.QuizMetadata;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.FinishActivity.NETWORK_DOWN;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class QuizActivity extends AppActivity {

    private static String TAG = "QuizActivity";

    public Firebase mQuestionRef, mChoice1Ref, mChoice2Ref, mChoice3Ref, mChoice4Ref, mAnswerRef;
    public Button nextButton;
    public RadioButton mRadio1, mRadio2, mRadio3, mRadio4;
    public RadioGroup rg;
    public String mAnswer;
    public int questionCounter = 0;
    public int score = 0;
    public Integer mQuestionNumber[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    public long timeLeftInMillis = 1200000;
    public Boolean isClicked = false;
    QuizMetadata quizMetadata = new QuizMetadata(0, 0, 0, new Date());
    ArrayList<Integer> list;
    ArrayList<String> question = new ArrayList<>();

    int c = 0;
    User user = null;
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
            navigateOut(FinishActivity.TIME_UP);
        }
    }.start();
    private TextView mQuestion, quesNum, timer;

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


        //get the question first
        updateQuestion();


        /*
         * Fetch user information first before entering into quiz
         */
        UserHandler.getInstance().getUser((user, flag) -> {
            switch (flag) {
                case UPDATED:
                    QuizActivity.this.user = user;
                    break;
                case FAILED:
                    //TODO Show dismissible dialog than Toast
                    Toast.makeText(QuizActivity.this, "Quiz is not available for this user at this time, try again later", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        });


        nextButton.setOnClickListener(v -> {
            //if user is being fetched, don't submit yet
            if (user == null) {
                Toast.makeText(this, "Please wait while we load", Toast.LENGTH_LONG).show();
                return;
            }

            if (rg.getCheckedRadioButtonId() == -1) {
                Toast.makeText(QuizActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            if (questionCounter <= 11) {
                if (checkAnswer()) {
                    score++;

                    quizMetadata.setAnsweredCorrectly(score);
                    //TODO add multiplier for points
                    user.setPoints(score);
                    UserHandler.getInstance().getQuizUpdater()
                            .set(quizMetadata)
                            .update((quizMetadata, didUpdate) -> {
                                if (didUpdate) {
                                    Log.i(TAG, "Score updated");
                                } else {
                                    Toast.makeText(this, "Score update failed, you have been disconnected. Please try again later", Toast.LENGTH_LONG).show();
                                    navigateOut(NETWORK_DOWN);
                                }
                            });
                }

                //finally update the question
                if (questionCounter == 10) {
                    String ans = "Submit";
                    nextButton.setText(ans);
                } else if (questionCounter == 11) {
                    isClicked = true; //FIXME ?why?
                    //checkAnswer();
                    navigateOut(FinishActivity.QUIZ_COMPLETED);
                    finish();
                }

                questionCounter++;
                c++;

                quizMetadata.setAttended(questionCounter);

                UserHandler.getInstance().setUser(user, (user, flags) -> {
                    switch (flags) {
                        case FAILED:
                            //TODO use dismissible dialog than Toast
                            Toast.makeText(QuizActivity.this, "Quiz submit failed, please try again later", Toast.LENGTH_LONG).show();
                            finish();
                    }
                });
                if (questionCounter < 12)
                    updateQuestion();
            }
        });
    }

    void navigateOut(int flag) {
        Intent intent = new Intent(QuizActivity.this, FinishActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("type", flag);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        navigateOut(FinishActivity.BACK_PRESSED);
    }

    protected void onUserLeaveHint() {
        if (!isClicked) {
            navigateOut(FinishActivity.BACK_PRESSED);
            super.onUserLeaveHint();
        }
    }

    public void updateQuestion() {

        rg.clearCheck();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String url = "url";
        switch (day) {
            case Calendar.MONDAY:
                url = "https://quizmaster-89038.firebaseio.com/0/iot/";
                break;
            case Calendar.TUESDAY:
                url = "https://quizmaster-89038.firebaseio.com/1/mobility/";
                break;
            case Calendar.WEDNESDAY:
                url = "https://quizmaster-89038.firebaseio.com/2/cloud/";
                break;
            case Calendar.THURSDAY:
                url = "https://quizmaster-89038.firebaseio.com/3/bigdata/";
                break;
            case Calendar.FRIDAY:
                url = "https://quizmaster-89038.firebaseio.com/4/frontend/";
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
                for(DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    String s = snapShot.getValue(String.class);
                    question.add(s);
                }
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

        String ans = "Question: " + (questionCounter + 1) + "/" + 12;
        quesNum.setText(ans);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public boolean checkAnswer() {
        int selected = rg.getCheckedRadioButtonId();
        if (selected == -1) {
            return false;
        }
        RadioButton rbSelected = findViewById(selected);
        return rbSelected.getText().equals(mAnswer);
    }
}
