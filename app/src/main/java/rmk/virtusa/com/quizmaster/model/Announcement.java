package rmk.virtusa.com.quizmaster.model;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Announcement {

    private String firebaseUid;

    private Boolean isAnonymousPost;

    private String title;

    private String message;

    private List<String> attachments;

    private Date announceDate;

    private Date expiryDate;

    public Announcement() {

    }

    public Announcement(String firebaseUid, Boolean isAnonymousPost, String title, String message, List<String> attachments, Date announceDate, Date expiryDate) {
        this.isAnonymousPost = isAnonymousPost;
        this.message = message;
        this.attachments = attachments;
        this.firebaseUid = firebaseUid;
        this.title = title;
        this.expiryDate = expiryDate;
        this.announceDate = announceDate;
    }

    public String getFirebaseUid() {
        return this.firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public Boolean getAnonymousPost() {
        return isAnonymousPost;
    }

    public void setAnonymousPost(Boolean anonymousPost) {
        isAnonymousPost = anonymousPost;
    }

    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getMessage() {
        return this.message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public List<String> getAttachments() {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        return this.attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }


    public Date getAnnounceDate() {
        return this.announceDate;
    }


    public void setAnnounceDate(Date announceDate) {
        this.announceDate = announceDate;
    }


    public Date getExpiryDate() {
        return this.expiryDate;
    }


    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }


}
