package rmk.virtusa.com.quizmaster.model;


import java.util.List;

public class Branch {

    private String name;
    private String moto;
    private List<String> admins;

    public Branch() {

    }

    public Branch(String name, String moto, List<String> admins) {
        this.name = name;
        this.moto = moto;
        this.admins = admins;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoto() {
        return this.moto;
    }

    public void setMoto(String moto) {
        this.moto = moto;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }
}
