package rmk.virtusa.com.quizmaster.handler;

import rmk.virtusa.com.quizmaster.model.User;

/**
 * Created by sriram on 08/07/18.
 */

public class ResourceHandler {
    private static final ResourceHandler ourInstance = new ResourceHandler();

    public static ResourceHandler getInstance() {
        return ourInstance;
    }


    private String TAG = "ResourceHandler";

    private User user;

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }


    private ResourceHandler() {
    }
}
