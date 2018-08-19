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
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import rmk.virtusa.com.quizmaster.handler.UserHandler;
import rmk.virtusa.com.quizmaster.model.QuizMetadata;
import rmk.virtusa.com.quizmaster.model.User;

import static rmk.virtusa.com.quizmaster.FinishActivity.NETWORK_DOWN;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.FAILED;
import static rmk.virtusa.com.quizmaster.handler.UserHandler.UPDATED;

public class QuizActivity extends AppActivity {

    private static String TAG = "QuizActivity";

    public Firebase mQuestionRef;
    public Button nextButton;
    public RadioButton mRadio1, mRadio2, mRadio3, mRadio4;
    public RadioGroup rg;
    public int questionCounter = 0;
    public int score = 0;
    public long timeLeftInMillis = 1200000;
    public Boolean isClicked = false;
    QuizMetadata quizMetadata = new QuizMetadata(0, 0, 0, new Date());
    public ArrayList<Question> questions = new ArrayList<>();
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

        mRadio1 = findViewById(R.id.choice1);
        mRadio2 = findViewById(R.id.choice2);
        mRadio3 = findViewById(R.id.choice3);
        mRadio4 = findViewById(R.id.choice4);
        rg = findViewById(R.id.radio);

        timer = findViewById(R.id.text_view_countdown);
        quesNum = findViewById(R.id.question_num);
        mQuestion = findViewById(R.id.question);
        nextButton = findViewById(R.id.next);

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

        /*UserHandler.getInstance().getUser((user, flag)-> {
            switch (flag) {
                case UPDATED:
                    user.getBranch();
                    break;
                case FAILED:
                    Toast.makeText(QuizActivity.this, "Quiz is not available for this user at this time, try again later", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        });*/

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

            if (questionCounter <= questions.size()-1) {
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
                if (questionCounter == questions.size()-2) {
                    String ans = "Submit";
                    nextButton.setText(ans);
                } else if (questionCounter == questions.size()-1) {
                    isClicked = true; //FIXME ?why?
                    //checkAnswer();
                    navigateOut(FinishActivity.QUIZ_COMPLETED);
                    finish();
                }

                quizMetadata.setAttended(questionCounter);

                UserHandler.getInstance().setUser(user, (user, flags) -> {
                    switch (flags) {
                        case FAILED:
                            //TODO use dismissible dialog than Toast
                            Toast.makeText(QuizActivity.this, "Quiz submit failed, please try again later", Toast.LENGTH_LONG).show();
                            finish();
                    }
                });
                if (questionCounter < questions.size())
                    advance();
            }
        });

            String branch = user.getBranch() == null || user.getBranch().isEmpty() ? "Other" : user.getBranch();

            Firebase.setAndroidContext(this);

            switch (branch) {
                case "Other":
                    mQuestionRef = new Firebase("https://quizmaster-89038.firebaseio.com/0/other");
                    break;
                case "Mobility":
                    mQuestionRef = new Firebase("https://quizmaster-89038.firebaseio.com/1/mobility");
                    break;
                case "Cloud":
                    mQuestionRef = new Firebase("https://quizmaster-89038.firebaseio.com/2/cloud");
                    break;
                case "Big Data":
                    mQuestionRef = new Firebase("https://quizmaster-89038.firebaseio.com/3/bigdata");
                    break;
                case "Front End":
                    mQuestionRef = new Firebase("https://quizmaster-89038.firebaseio.com/4/frontend");
                    break;
                default:
                    mQuestionRef = new Firebase("https://quizmaster-89038.firebaseio.com/0/other");
            }

            mQuestionRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()) {
                        Question question = child.getValue(Question.class);
                        questions.add(question);
                    }

                    Collections.shuffle(questions);
                    questionCounter = 0;
                    displayQuestion(questionCounter);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(QuizActivity.this, "Failed Loading Questions", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void displayQuestion(int index) {
        String ans = "Question: " + (index + 1) + "/" + (questions.size());
        quesNum.setText(ans);
        rg.clearCheck();
        mQuestion.setText(questions.get(index).getQuestion());
        mRadio1.setText(questions.get(index).getChoice1());
        mRadio2.setText(questions.get(index).getChoice2());
        mRadio3.setText(questions.get(index).getChoice3());
        mRadio4.setText(questions.get(index).getChoice4());
    }

    public void advance() {
        questionCounter = (questionCounter + 1) % questions.size();
        displayQuestion(questionCounter);
    }

    public boolean checkAnswer() {
        int selected = rg.getCheckedRadioButtonId();
        if (selected == -1) {
            return false;
        }
        RadioButton rbSelected = findViewById(selected);
        return rbSelected.getText().equals(questions.get(questionCounter).getAnswer());
    }
}
