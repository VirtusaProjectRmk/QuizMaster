package rmk.virtusa.com.quizmaster.model;


import java.util.ArrayList;
import java.util.List;

public class Inbox {


    private String name;

    private String status;

    private List<String> receivers;

    public Inbox(){

    }

    public Inbox(String name, String status, List<String> receivers) {
        this.status = status;
        this.name = name;
        this.receivers = receivers;
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


    public List<String> getReceivers() {
        if (this.receivers == null) {
            this.receivers = new ArrayList<>();
        }
        return this.receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }


}
