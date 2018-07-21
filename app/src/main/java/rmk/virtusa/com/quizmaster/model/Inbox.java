package rmk.virtusa.com.quizmaster.model;


import java.util.ArrayList;
import java.util.List;

public class Inbox {

    private String inboxId;
    private String name;
    private String inboxImage;
    private String status;
    private List<String> userIds;

    public Inbox() {

    }

    public Inbox(String inboxId, String name, String inboxImage, String status, List<String> userIds) {
        this.inboxId = inboxId;
        this.status = status;
        this.name = name;
        this.inboxImage = inboxImage;
        this.userIds = userIds;
    }

    public Inbox(String name, String status, List<String> userIds) {
        this.status = status;
        this.name = name;
        this.userIds = userIds;
    }

    public String getInboxImage() {
        return inboxImage;
    }

    public void setInboxImage(String inboxImage) {
        this.inboxImage = inboxImage;
    }

    public String getInboxId() {
        return inboxId;
    }

    public void setInboxId(String inboxId) {
        this.inboxId = inboxId;
    }

    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getStatus() {
        return this.status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public List<String> getUserIds() {
        if (this.userIds == null) {
            this.userIds = new ArrayList<>();
        }
        return this.userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }


}
