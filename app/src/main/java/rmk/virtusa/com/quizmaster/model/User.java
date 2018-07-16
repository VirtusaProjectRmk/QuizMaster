package rmk.virtusa.com.quizmaster.model;


import java.util.List;

public class User {


    private String id;

    private String firebaseUid;

    private String name;

    private String branch;

    private int qAnsTot;

    private int aAttTot;

    private int pointsTot;

    private List<String> inboxes;

    public User() {

    }

    public User(int aAttTot, int qAnsTot, int pointsTot, String id, String firebaseUid, String name, String branch, List<String> inboxes) {
        this.qAnsTot = qAnsTot;
        this.aAttTot = aAttTot;
        this.id = id;
        this.firebaseUid = firebaseUid;
        this.pointsTot = pointsTot;
        this.name = name;
        this.branch = branch;
        this.inboxes = inboxes;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getQAnsTot() {
        return this.qAnsTot;
    }


    public void setQAnsTot(Integer qAnsTot) {
        this.qAnsTot = qAnsTot;
    }


    public int getAAttTot() {
        return this.aAttTot;
    }


    public void setAAttTot(Integer aAttTot) {
        this.aAttTot = aAttTot;
    }


    public int getPointsTot() {
        return this.pointsTot;
    }


    public void setPointsTot(Integer pointsTot) {
        this.pointsTot = pointsTot;
    }

    public List<String> getInboxes() {
        return inboxes;
    }

    public void setInboxes(List<String> inboxes) {
        this.inboxes = inboxes;
    }
}
