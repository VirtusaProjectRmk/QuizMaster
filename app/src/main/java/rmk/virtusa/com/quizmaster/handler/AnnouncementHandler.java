package rmk.virtusa.com.quizmaster.handler;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.SetOptions;

import java.util.List;

import rmk.virtusa.com.quizmaster.model.Announcement;

public class AnnouncementHandler {
    public static final int UPDATED = 0;
    public static final int FAILED = 1;
    private static final String TAG = "AnnouncementHandler";
    private static final AnnouncementHandler ourInstance = new AnnouncementHandler();
    OnAddedNew onAddedNew;
    private CollectionReference announcementCollectionRef = null;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private AnnouncementHandler() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        announcementCollectionRef = db.collection("announcements");
        announcementCollectionRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen Failed: " + e.getMessage());
                return;
            }
        });
    }

    public static AnnouncementHandler getInstance() {
        return ourInstance;
    }

    public void addAnnouncement(Announcement announcement) {
        announcementCollectionRef.document()
                .set(announcement, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if (onAddedNew != null) {
                        onAddedNew.onAddedNew(announcement);
                    }
                })
                .addOnFailureListener(e -> {
                    onAddedNew.onAddedNew(null);
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

    public void setAddedNew(OnAddedNew onAddedNew) {
        this.onAddedNew = onAddedNew;
    }

    public interface OnAnnouncementUpdateListener {
        void onAnnouncementUpdate(Announcement announcement, int flags);
    }

    public interface OnAddedNew {
        void onAddedNew(Announcement announcement);
    }
}
