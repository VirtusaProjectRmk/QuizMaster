package rmk.virtusa.com.quizmaster.handler;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.common.io.ByteStreams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import rmk.virtusa.com.quizmaster.model.Detail;
import rmk.virtusa.com.quizmaster.model.Gender;
import rmk.virtusa.com.quizmaster.model.Link;
import rmk.virtusa.com.quizmaster.model.QuizMetadata;
import rmk.virtusa.com.quizmaster.model.User;

/*
 * UserHandler does the following tasks asynchronously for updating the
 * cache of information to be updated as possible,
 * Updates the users cache every 45secs or when getUsers() is called explicitly
 *
 */
public class UserHandler {

    public static final int UPDATED = 0;
    public static final int FAILED = 1;

    private static final String TAG = "UserHandler";
    private static final UserHandler ourInstance = new UserHandler();
    /*
     * Cached users list for leaderboard purposes
     */
    private CollectionReference userCollectionRef = null;
    private DocumentReference userRef = null;
    //private CollectionReference userContactCollectionRef = null;
    //private CollectionReference userDetailCollectionRef = null;
    private CollectionReference userQuizCollectionRef = null;
    //private CollectionReference userInboxCollection = null;

    private UserUpdater<QuizMetadata> quizUpdater;
    private FirestoreList<Link> linkList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private User user = null;
    private String userUid = "";

    private UserHandler() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "Fatal error");
            return;
        }
        userUid = auth.getCurrentUser().getUid();

        userCollectionRef = db.collection("users");
        userRef = userCollectionRef.document(userUid);

        quizUpdater = new UserUpdater<>(userRef.collection("quizzes").getPath());

        linkList = new FirestoreList<>(Link.class, userRef.collection("links"), (link, didUpdate) -> {
            //TODO indicate UserHandler that list has loaded
        });

        //userContactCollectionRef = userRef.collection("contacts");
        //userDetailCollectionRef = userRef.collection("details");
        //userInboxCollection = userRef.collection("inboxes");

        getUser(userUid, (user, flag) -> {
            UserHandler.this.user = user;
        });
    }

    public FirestoreList<Link> getLinkList() {
        return linkList;
    }

    public static UserHandler getInstance() {
        return ourInstance;
    }

    public UserUpdater<QuizMetadata> getQuizUpdater() {
        return quizUpdater;
    }

    public String getUserUid() {
        return userUid;
    }

    public void getUsers(OnUpdateUserListener onUpdateUserListener) {
        //TODO call method as soon as we get a user than fetching all users at a time
        userCollectionRef
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    for (User user : users) {
                        if (user == null) {
                            onUpdateUserListener.onUserUpdate(null, FAILED);
                        }
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
        imagesRef.child(getUser().getUserUid())
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
        return "";
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
                        User user = new User(firebaseUid, "", auth.getCurrentUser().getDisplayName(), 0, "", "", Gender.OTHER, "", null);
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
     * Gets the user based on the specified userUid
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


    public void updateUserWithQuiz(QuizMetadata quizMetadata) {
        userCollectionRef.document(userUid).collection("quiz")
                .add(quizMetadata);
        getUser((user, flag) -> {
            setUser(user, (usr, flg) -> {
                int points = quizMetadata.getAnsweredCorrectly() * quizMetadata.getMultiplier();
                usr.setPoints(user.getPoints() + points);
            });
        });
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
                        User user = new User(auth.getCurrentUser().getUid(), "", auth.getCurrentUser().getDisplayName(), 0, "", "", Gender.OTHER, "", null);
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

    void addDetail(Detail detail, InboxHandler.OnUpdateInboxListener onUpdateInboxListener) {
        //onUpdateInboxListener.onUpdateInbox(detail, UPDATED);
    }

    public interface OnUpdateUserListener {
        public void onUserUpdate(User user, int flags);
    }

}
