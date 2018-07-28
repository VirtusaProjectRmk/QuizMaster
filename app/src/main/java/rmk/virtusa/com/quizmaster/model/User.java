package rmk.virtusa.com.quizmaster.model;


import java.util.List;

public class User {
    private String id;
    private String firebaseUid;
    private String name;
    private String summary;
    private String displayImage;
    private int points;
    private String branch;
    private Gender gender;
    private List<String> inboxes;
    //quiz
    //request

    public User() {
    }

    public User(String firebaseUid, String id, String name, int points, String displayImage, String summary, Gender gender, String branch, List<String> inboxes) {
        this.firebaseUid = firebaseUid;
        this.id = id;
        this.name = name;
        this.points = points;
        this.displayImage = displayImage;
        this.summary = summary;
        this.gender = gender;
        this.branch = branch;
        this.inboxes = inboxes;
    }

    public List<String> getInboxes() {
        return inboxes;
    }

    public void setInboxes(List<String> inboxes) {
        this.inboxes = inboxes;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirebaseUid() {
        return this.firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDisplayImage() {
        return this.displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getBranch() {
        return this.branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

}
