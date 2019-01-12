package rmk.virtusa.com.quizmaster.handler;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.common.io.ByteStreams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rmk.virtusa.com.quizmaster.model.Announcement;
import rmk.virtusa.com.quizmaster.model.Link;
import rmk.virtusa.com.quizmaster.model.QuizMetadata;
import rmk.virtusa.com.quizmaster.model.User;

/*
 * UserHandler provides popular functions used throughout the app
 * org.greenrobot.eventbus.EventBus is used as the medium to send requested object
 */
public class UserHandler {
    public static final int UPDATED = 0;
    public static final int FAILED = 1;

    private static final String TAG = "UserHandler";
    private static UserHandler instance;

    public CollectionReference usersRef = FirebaseFirestore.getInstance()
            .collection("users");
    public CollectionReference announcementsRef = FirebaseFirestore.getInstance()
            .collection("announcements");

    private CollectionReference adminsRef = FirebaseFirestore.getInstance()
            .collection("admins");
    private CollectionReference userQuizCollectionRef = null;
    private UserUpdater<QuizMetadata> quizUpdater;
    private SharedPreferences preferences;
    private boolean isAdmin = false;

    public boolean getIsAdmin() {
        return isAdmin;
    }

    private UserHandler() {
        if (UserHandler.getUserId().isEmpty()) {
            Log.e(TAG, "Fatal error");
            return;
        }

        quizUpdater = new UserUpdater<>(usersRef.document(UserHandler.getUserId()).
                collection("quizzes").getPath());
    }

    public static UserHandler getInstance() {
        if (instance == null) {
            instance = new UserHandler();
        }
        return instance;
    }


    public static String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void listenForAnnouncements() {
        announcementsRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            for (DocumentChange dC : queryDocumentSnapshots.getDocumentChanges()) {
                Announcement ann = dC.getDocument().toObject(Announcement.class);
                switch (dC.getType()) {
                    case ADDED:
                        EventBus.getDefault().post(ann);
                        break;
                    case REMOVED:
                        ann.setUserId("");
                        EventBus.getDefault().post(ann);
                }
            }
        });
    }

    public void getAnnouncements() {
        announcementsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Announcement> announcements = queryDocumentSnapshots.toObjects(Announcement.class);
            EventBus.getDefault().post(announcements);
        });
    }

    public UserUpdater<QuizMetadata> getQuizUpdater() {
        return quizUpdater;
    }

    public String getUserUid() {
        return FirebaseAuth.getInstance().getUid();
    }

    public void getUserQuizData(String userId) {
        usersRef.document(userId).collection("quizzes").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<QuizMetadata> qm = queryDocumentSnapshots.toObjects(QuizMetadata.class);
                    EventBus.getDefault().post(qm);
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

    public void getUser(@NonNull String firebaseUid) {
        if (firebaseUid.isEmpty()) {
            EventBus.getDefault().post(new User());
            return;
        }
        usersRef.document(firebaseUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        EventBus.getDefault().post(user == null ? new User() : user);
                    }
                })
                .addOnFailureListener(e -> EventBus.getDefault().post(new User()));
    }


    public void updateUserWithQuiz(User user, QuizMetadata quizMetadata) {
        usersRef.document(UserHandler.getUserId()).collection("quiz")
                .add(quizMetadata);
        setUser(user, (usr, flg) -> {
            int points = quizMetadata.getAnsweredCorrectly() * quizMetadata.getMultiplier();
            usr.setPoints(user.getPoints() + points);
        });
    }


    public void getUser() {
        adminsRef.document(UserHandler.getUserId()).get().addOnSuccessListener(q -> {
            if (q.exists()) {
                isAdmin = true;
            }
        }).addOnFailureListener(e -> {
            isAdmin = false;
        });
        usersRef
                .document(UserHandler.getUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user == null) {
                            EventBus.getDefault().post(new User());
                            return;
                        }
                        EventBus.getDefault().post(user);
                    } else {
                        //if user object does'nt exist, create one
                        User user = new User();
                        user.setId(UserHandler.getUserId());
                        usersRef.document(UserHandler.getUserId())
                                .set(user, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    EventBus.getDefault().post(user);
                                    Log.i(TAG, "Added new user to firestore");
                                })
                                .addOnFailureListener(e -> {
                                    EventBus.getDefault().post(new User());
                                    Log.i(TAG, "Adding registration failed, contact administrator if the problem persists");
                                });
                    }
                })
                .addOnFailureListener(e -> EventBus.getDefault().post(new User()));
    }

    //TODO: Remove FirestoreList all together
    public FirestoreList<Link> getLinks(String userId, FirestoreList.OnLoadListener<Link> onLoadListener) {
        return new FirestoreList<Link>(Link.class, usersRef.document(userId).collection("links"), onLoadListener);
    }

    public FirestoreList<QuizMetadata> getUserQuizData(@NonNull String userUid, @NonNull FirestoreList.OnLoadListener<QuizMetadata> onLoadListener) {
        return new FirestoreList<>(QuizMetadata.class, usersRef.document(userUid).collection("quizzes"), onLoadListener);
    }


    //FIXME: WARNING: The following functions were created only because of time constrains, if given chance destroy it, with fire if possible

    public Task<QuerySnapshot> getUsers(String branchId) {
        return usersRef.whereEqualTo("branchId", branchId).get();
    }

    public void setUser(User user, OnUpdateUserListener onUpdateUserListener) {
        if (user == null) {
            onUpdateUserListener.onUserUpdate(user, FAILED);
            return;
        }
        if (user.getId().isEmpty()) {
            onUpdateUserListener.onUserUpdate(user, FAILED);
            return;
        }
        usersRef.document(user.getId())
                .set(user, SetOptions.merge())
                .addOnSuccessListener(aVoid -> onUpdateUserListener.onUserUpdate(user, UPDATED))
                .addOnFailureListener(e -> onUpdateUserListener.onUserUpdate(user, FAILED));
    }

    public void getUsersByBranch(String branch, OnUpdateUserListener onUpdateUserListener) {
        if (branch == null || branch.isEmpty()) {
            branch = "Other";
        }
        usersRef.whereEqualTo("branch", branch)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    for (User user : users) {
                        if (user == null) {
                            onUpdateUserListener.onUserUpdate(null, FAILED);
                        } else {
                            onUpdateUserListener.onUserUpdate(user, UPDATED);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    onUpdateUserListener.onUserUpdate(null, FAILED);
                });
    }

    public interface OnUpdateUserListener {
        public void onUserUpdate(User user, int flag);
    }

}
