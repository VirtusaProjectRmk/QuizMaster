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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

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

    List<User> users = new ArrayList<>();

    public User getUser(){
        return user;
    }

    public List<User> getUsers(){
        updateUsersCache();
        return users;
    }

    private Task<QuerySnapshot> updateUsersCache(){
        CollectionReference usersRef = db.collection("users");
        Task<QuerySnapshot> snapshotTask = usersRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            List<User> types = queryDocumentSnapshots.toObjects(User.class);
                            users = types;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failure");
                    }
                });
        return snapshotTask;
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
        updateUsersCache();
    }
}
