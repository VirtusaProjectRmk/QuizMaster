package rmk.virtusa.com.quizmaster.handler;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rmk.virtusa.com.quizmaster.model.User;

/*
 * ResourceHandler does the following tasks asynchronously for updating the
 * cache of information to be updated as possible,
 * Updates the users cache every 45secs or when getUsers() is called explicitly
 *
 */
public class ResourceHandler {
    private static final String TAG = "ResourceHandler";
    private static final ResourceHandler ourInstance = new ResourceHandler();

    public static ResourceHandler getInstance() {
        return ourInstance;
    }

    FirebaseFirestore db;
    FirebaseAuth auth;

    String dp;

    void setDp(InputStream is) {

    }

    void setDp(String dp) {
        this.dp = dp;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/");
        //add
        imagesRef.child(getUser().getFirebaseUid())
                .putFile(null)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Log.i(TAG, "Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Success");
                    }
                });

    }

    String getDp() {
        return dp;
    }

    private User user = new User(0, 0, 0, "", "", "", "");

    /*
     * Cached users list for leaderboard purposes
     */
    List<User> users = new ArrayList<>();

    @Nullable
    public User getUser(String firebaseUid) {
        if (getUser().getFirebaseUid().equals(firebaseUid)) return getUser();
        for (User user : getUsers()) {
            if (user.getFirebaseUid() == null) continue;
            if (user.getFirebaseUid().equals(firebaseUid)) {
                return user;
            }
        }
        return null;
    }

    public User getUser() {
        if (user == null) {
            updateUserFromAuth();
        }
        return user;
    }

    /*
     * Returns a cached list of users, might not always reflect the Firestore absolutely everytime
     */
    public List<User> getUsers() {
        updateUsersCache();
        return users;
    }

    /*
     * Updates the locally used global user object from Firestore if exists
     * else creates one based on the FirebaseAuth
     */
    public void updateUserFromAuth() {
        final CollectionReference cRef = db.collection("users");
        DocumentReference dRef = cRef.document(auth.getCurrentUser().getUid());
        dRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.i(TAG, "Success");
                        //if user object exists, get for local use
                        if (documentSnapshot.exists()) {
                            try {
                                User user = documentSnapshot.toObject(User.class);
                                if (user == null) {
                                    Log.e(TAG, "FATAL Error while retreiving user");
                                }
                                ResourceHandler.getInstance().setUser(user);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        } else {
                            //if user object does'nt exist, create one
                            User user = new User(0, 0, 0, "", auth.getCurrentUser().getUid(), auth.getCurrentUser().getDisplayName(), "");
                            cRef.document(auth.getCurrentUser().getUid())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(TAG, "Added new user to firestore");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG, "Adding registration failed, contact administrator if the problem persists");
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Failed");
                    }
                });
    }

    /*
     * Updates the global users cache
     * Documents are fetched from firestore if they exist
     * and saved to instance variable users
     */
    //TODO synchronize only changes rather than downloading and uploading the whole list of users everytime
    private Task<QuerySnapshot> updateUsersCache() {
        CollectionReference usersRef = db.collection("users");
        Task<QuerySnapshot> snapshotTask = usersRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
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


    /*
     * sets the given user object to the document with id of the FirebaseAuth user
     */
    //TODO add return type of Task<Boolean> for handling success and failure cases
    public void setUser(User user) {
        if (auth.getCurrentUser() == null) return;
        DocumentReference dRef = db.collection("users").document(auth.getCurrentUser().getUid());
        //if the user name changes in object update in FirebaseAuth
        if (!user.getName().equals(this.user.getName())) {
            UserProfileChangeRequest userUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getName())
                    .build();
            auth.getCurrentUser().updateProfile(userUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.e(TAG, "User name update failed");
                            } else {
                                Log.i(TAG, "User name update success");
                            }
                        }
                    });
        }
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
                        Log.e(TAG, "Cache update failed");
                    }
                });
        this.user = user;
    }

    public void incQAnsTot() {
        user.setQAnsTot(user.getQAnsTot() + 1);
        setUser(user);
    }

    public void incAAttTot() {
        user.setAAttTot(user.getAAttTot() + 1);
        setUser(user);
    }

    public void incPointsTot(int points) {
        user.setPointsTot(user.getPointsTot() + points);
        setUser(user);
    }

    class CacheUpdateTask extends TimerTask {
        @Override
        public void run() {
            updateUsersCache();
        }
    }

    ;

    private ResourceHandler() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Timer timer = new Timer();
        timer.schedule(new CacheUpdateTask(), 45 * 1000);
    }
}
