package rmk.virtusa.com.quizmaster.model;

public class Link {
    private String icon;
    private String url;

    public Link() {

    }

    public Link(String icon, String url) {
        this.icon = icon;
        this.url = url;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
