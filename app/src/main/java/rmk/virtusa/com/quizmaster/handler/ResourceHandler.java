package rmk.virtusa.com.quizmaster.handler;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.common.io.ByteStreams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import rmk.virtusa.com.quizmaster.model.Announcement;
import rmk.virtusa.com.quizmaster.model.User;

/*
 * ResourceHandler does the following tasks asynchronously for updating the
 * cache of information to be updated as possible,
 * Updates the users cache every 45secs or when getUsers() is called explicitly
 *
 */
public class ResourceHandler {

    public static final int UPDATED = 0;
    public static final int FAILED = 1;
    public static final int OVERTIME = 2;

    private static final String TAG = "ResourceHandler";
    private static final ResourceHandler ourInstance = new ResourceHandler();
    /*
     * Cached users list for leaderboard purposes
     */
    List<User> users = new ArrayList<>();
    List<Announcement> announcements = new ArrayList<>();
    private CollectionReference userCollectionRef = null;
    private CollectionReference announcementCollectionRef = null;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private User user = null;

    private ResourceHandler() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        userCollectionRef = db.collection("users");
        announcementCollectionRef = db.collection("announcements");

        getUser(auth.getCurrentUser().getUid(), (user, flag) -> {
            ResourceHandler.this.user = user;
        });

    }

    public static ResourceHandler getInstance() {
        return ourInstance;
    }

    public void getUsers(OnUpdateUserListener onUpdateUserListener) {
        //TODO call method as soon as we get a user than fetching all users at a time
        userCollectionRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    for (User user : users) {
                        onUpdateUserListener.onUserUpdate(user, UPDATED);
                    }
                })
                .addOnFailureListener(e -> {
                    onUpdateUserListener.onUserUpdate(null, FAILED);
                });
    }

    /*
     * TODO save to device then upload to FirebaseStorage
     * Upload image with current firebase Uid
     * Returns the uploaded files reference string
     */
    String uploadImage(ByteStreams byteStreams) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/");
        //add
        /*
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
        */
        return new String();
    }

    public void updateUserFromAuth(@NonNull String firebaseUid, @NonNull OnUpdateUserListener onUpdateUserListener) {
        userCollectionRef.document(firebaseUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.e(TAG, "User already exist");
                        User user = documentSnapshot.toObject(User.class);
                        onUpdateUserListener.onUserUpdate(user, UPDATED);
                    } else {
                        User user = new User(0, 0, 0, "", firebaseUid, "", "", "", new ArrayList<>());
                        userCollectionRef.document(firebaseUid)
                                .set(user, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    onUpdateUserListener.onUserUpdate(user, UPDATED);
                                })
                                .addOnFailureListener(e -> {
                                    onUpdateUserListener.onUserUpdate(null, FAILED);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Cannot get user info");
                    onUpdateUserListener.onUserUpdate(null, FAILED);
                });
    }

    /*
     * Gets the user based on the specified firebaseUid
     */
    public void getUser(@NonNull String firebaseUid, @NonNull OnUpdateUserListener onUpdateUserListener) {
        if (firebaseUid.isEmpty()) {
            onUpdateUserListener.onUserUpdate(null, FAILED);
            return;
        }
        userCollectionRef.document(firebaseUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onUpdateUserListener.onUserUpdate(documentSnapshot.toObject(User.class), UPDATED);
                    }
                })
                .addOnFailureListener(e -> onUpdateUserListener.onUserUpdate(null, FAILED));
    }

    /*
     * Queries databases for a list of users and returns them through callback
     */
    public void getUsers(List<String> userIds, OnUpdateUserListener onUpdateUser) {
        for (String userId : userIds) {
            getUser(userId, onUpdateUser);
        }
    }

    /*
     * Gets the user registered in firebase auth
     */
    public void getUser(@NonNull OnUpdateUserListener onUpdateUserListener) {
        userCollectionRef
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onUpdateUserListener.onUserUpdate(documentSnapshot.toObject(User.class), UPDATED);
                    } else {
                        //if user object does'nt exist, create one
                        User user = new User(0, 0, 0, "", auth.getCurrentUser().getUid(), auth.getCurrentUser().getDisplayName(), "", "", new ArrayList<>());
                        userCollectionRef.document(auth.getCurrentUser().getUid())
                                .set(user, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    onUpdateUserListener.onUserUpdate(user, UPDATED);
                                    Log.i(TAG, "Added new user to firestore");
                                })
                                .addOnFailureListener(e -> {
                                    onUpdateUserListener.onUserUpdate(user, FAILED);
                                    Log.i(TAG, "Adding registration failed, contact administrator if the problem persists");
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onUpdateUserListener.onUserUpdate(user, FAILED);
                    }
                });
    }

    /*
     * Sets the user registered in firebase auth
     */
    public void setUser(User user, OnUpdateUserListener onUpdateUserListener) {
        CollectionReference usersCollectionRef = db.collection("users");
        if (user == null) {
            onUpdateUserListener.onUserUpdate(user, FAILED);
            return;
        }
        if (user.getFirebaseUid() == null) {
            onUpdateUserListener.onUserUpdate(user, FAILED);
            return;
        }
        //if the user name changes in object update in FirebaseAuth
        if (!user.getName().equals(this.user.getName())) {
            UserProfileChangeRequest userUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user.getName())
                    .build();
            auth.getCurrentUser().updateProfile(userUpdate)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "User name update failed");
                        } else {
                            Log.i(TAG, "User name update success");
                        }
                    });
        }
        usersCollectionRef.document(user.getFirebaseUid())
                .set(user, SetOptions.merge())
                .addOnSuccessListener(aVoid -> onUpdateUserListener.onUserUpdate(user, UPDATED))
                .addOnFailureListener(e -> onUpdateUserListener.onUserUpdate(user, FAILED));
    }

    public void addAnnouncement(Announcement announcement, OnAnnouncementUpdateListener onAnnouncementUpdateListener) {
        announcementCollectionRef.document()
                .set(announcement, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    onAnnouncementUpdateListener.onAnnouncementUpdate(announcement, UPDATED);
                })
                .addOnFailureListener(e -> {
                    onAnnouncementUpdateListener.onAnnouncementUpdate(announcement, FAILED);
                });
    }

    /*
     * Listener is called for every announcemet
     */
    public void getAnnouncements(OnAnnouncementUpdateListener onAnnouncementUpdateListener) {
        //FIXME only retreive elements if they are before expiry date
        announcementCollectionRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Announcement> announcements = queryDocumentSnapshots.toObjects(Announcement.class);
                    for (Announcement announcement : announcements) {
                        onAnnouncementUpdateListener.onAnnouncementUpdate(announcement, UPDATED);
                    }
                })
                .addOnFailureListener(e -> {
                    onAnnouncementUpdateListener.onAnnouncementUpdate(null, FAILED);
                });
        /*
        announcementRef
                .whereLessThan("expiryDate");
        */
    }

    public interface OnUpdateUserListener {
        public void onUserUpdate(User user, int flags);
    }

    public interface OnAnnouncementUpdateListener {
        public void onAnnouncementUpdate(Announcement announcement, int flags);
    }
}
