package rmk.virtusa.com.quizmaster.model;

import java.util.ArrayList;
import java.util.List;

public class Detail {

    private String type;

    private List<String> content;

    public Detail(String type, List<String> content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getContent() {
        if (this.content == null) {
            this.content = new ArrayList<>();
        }
        return this.content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }


}
