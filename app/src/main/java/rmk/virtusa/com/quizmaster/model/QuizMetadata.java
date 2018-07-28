package rmk.virtusa.com.quizmaster.model;

import java.util.Date;

public class QuizMetadata {

    private int attended;

    private int answeredCorrectly;

    private int multiplier;

    private Date dateTaken;

    public QuizMetadata() {

    }

    public QuizMetadata(int multiplier, int attended, int answeredCorrectly, Date dateTaken) {
        this.dateTaken = dateTaken;
        this.multiplier = multiplier;
        this.attended = attended;
        this.answeredCorrectly = answeredCorrectly;
    }

    public int getAttended() {
        return this.attended;
    }


    public void setAttended(Integer attended) {
        this.attended = attended;
    }

    public int getAnsweredCorrectly() {
        return this.answeredCorrectly;
    }

    public void setAnsweredCorrectly(Integer answeredCorrectly) {
        this.answeredCorrectly = answeredCorrectly;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public void setMultiplier(Integer multiplier) {
        this.multiplier = multiplier;
    }

    public Date getDateTaken() {
        return this.dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

}
