package rmk.virtusa.com.quizmaster.handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.util.List;

import rmk.virtusa.com.quizmaster.model.Announcement;

public class AnnouncementHandler {
    public static final int UPDATED = 0;
    public static final int FAILED = 1;
    private static final String TAG = "AnnouncementHandler";

    private static final AnnouncementHandler ourInstance = new AnnouncementHandler();
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
    }

    public static AnnouncementHandler getInstance() {
        return ourInstance;
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

    public interface OnAnnouncementUpdateListener {
        public void onAnnouncementUpdate(Announcement announcement, int flags);
    }
}
