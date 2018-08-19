package rmk.virtusa.com.quizmaster.model;

public class Report {

    private String reportedBy;
    private String comments;
    private String reportedUid;

    public Report(String reportedUid, String comments, String reportedBy) {
        this.reportedUid = reportedUid;
        this.comments = comments;
        this.reportedBy = reportedBy;
    }

    public String getReportedBy() {
        return this.reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReportedUid() {
        return this.reportedUid;
    }

    public void setReportedUid(String reportedUid) {
        this.reportedUid = reportedUid;
    }
}
