package rmk.virtusa.com.quizmaster.model;


import java.util.ArrayList;
import java.util.List;

public class Inbox {


    private String name;

    private String status;

    private List<String> userIds;

    public Inbox() {

    }

    public Inbox(String name, String status, List<String> userIds) {
        this.status = status;
        this.name = name;
        this.userIds = userIds;
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
