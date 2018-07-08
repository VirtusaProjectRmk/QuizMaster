package rmk.virtusa.com.quizmaster.handler;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import rmk.virtusa.com.quizmaster.model.User;

/**
 * Created by sriram on 08/07/18.
 */

public class ResourceHandler {
    private static final ResourceHandler ourInstance = new ResourceHandler();

    public static ResourceHandler getInstance() {
        return ourInstance;
    }

    FirebaseFirestore db;
    FirebaseAuth auth;

    private String TAG = "ResourceHandler";

    private User user;

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        DocumentReference dRef = db.collection("users").document(auth.getCurrentUser().getUid());
        dRef.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed");
                    }
                });
        this.user = user;
    }

    public void incQAnsTot(){
        user.setQAnsTot(user.getQAnsTot() + 1);
        setUser(user);
    }

    public void incAAttTot(){
        user.setAAttTot(user.getAAttTot() + 1);
        setUser(user);
    }

    public void incPointsTot(int points){
        user.setPointsTot(user.getPointsTot() + points);
        setUser(user);
    }

    private ResourceHandler() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }
}
