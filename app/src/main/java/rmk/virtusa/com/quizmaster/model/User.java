package rmk.virtusa.com.quizmaster.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private String id;


    @SerializedName("qansTot")
    @Expose
    private int qAnsTot;

    @SerializedName("qattTot")
    @Expose
    private int aAttTot;

    @SerializedName("ponitsTot")
    @Expose
    private int pointsTot;
    
    public User(){

    }

    public User(int aAttTot, int qAnsTot, int pointsTot, String id, String name) {
        this.qAnsTot = qAnsTot;
        this.aAttTot = aAttTot;
        this.id = id;
        this.pointsTot = pointsTot;
        this.name = name;
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
