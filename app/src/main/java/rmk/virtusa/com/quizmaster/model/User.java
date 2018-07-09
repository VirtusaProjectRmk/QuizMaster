package rmk.virtusa.com.quizmaster.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {


    private String id;

    private int qAnsTot;

    private int aAttTot;

    private int pointsTot;
    
    public User(){

    }

    public User(int aAttTot, int qAnsTot, int pointsTot, String id) {
        this.qAnsTot = qAnsTot;
        this.aAttTot = aAttTot;
        this.id = id;
        this.pointsTot = pointsTot;
    }
    
    public String getId() {
        return this.id;
    }
    
    
    public void setId(String id) {
        this.id = id;
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
    
    
    
    
}
