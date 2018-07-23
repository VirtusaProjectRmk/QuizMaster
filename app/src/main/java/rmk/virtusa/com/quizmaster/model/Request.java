package rmk.virtusa.com.quizmaster.model;

public class Request {

    private String requesterUid;

    private int requestDate;

    private int requestType;

    private String requestMessage;

    public Request() {

    }

    public Request(String requesterUid, int requestDate, String requestMessage, int requestType) {
        this.requesterUid = requesterUid;
        this.requestDate = requestDate;
        this.requestMessage = requestMessage;
        this.requestType = requestType;
    }

    public String getRequesterUid() {
        return this.requesterUid;
    }

    public void setRequesterUid(String requesterUid) {
        this.requesterUid = requesterUid;
    }

    public int getRequestDate() {
        return this.requestDate;
    }

    public void setRequestDate(Integer requestDate) {
        this.requestDate = requestDate;
    }

    public int getRequestType() {
        return this.requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }

    public String getRequestMessage() {
        return this.requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

}
