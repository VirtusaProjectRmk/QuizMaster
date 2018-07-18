package rmk.virtusa.com.quizmaster.handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.List;

import rmk.virtusa.com.quizmaster.model.Inbox;

public class InboxHandler {
    public static final int UPDATED = 0;
    public static final int FAILED = 1;
    public static final int EMPTY = 2;
    private static final InboxHandler ourInstance = new InboxHandler();
    private static final String TAG = "InboxHandler";

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CollectionReference inboxCollectionRef;

    private InboxHandler() {

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        inboxCollectionRef = db.collection("inboxes");
    }

    public static InboxHandler getInstance() {
        return ourInstance;
    }

    public void getInboxes(OnUpdateInboxListener onUpdateInbox) {
        ResourceHandler.getInstance().getUser((user, flag) -> {
            if (user.getInboxes() == null) {
                onUpdateInbox.onUpdateInbox(null, EMPTY);
                return;
            }
            for (String inboxId : user.getInboxes()) {
                inboxCollectionRef
                        .document(inboxId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Inbox inbox = documentSnapshot.toObject(Inbox.class);
                            if (inbox == null) {
                                onUpdateInbox.onUpdateInbox(inbox, UPDATED);
                            } else {
                                onUpdateInbox.onUpdateInbox(null, FAILED);
                            }
                        })
                        .addOnFailureListener(e -> {
                            onUpdateInbox.onUpdateInbox(null, FAILED);
                        });
            }
        });
    }

    public void createInbox(String firebaseId, OnUpdateInboxListener onUpdateInbox) {
        List<String> users = new ArrayList<>();
        users.add(firebaseId);
        users.add(auth.getCurrentUser().getUid());
        Inbox inbox = new Inbox("", "", users);
        List<String> inboxes = new ArrayList<>();
        DocumentReference dRef = inboxCollectionRef.document();
        inboxes.add(dRef.getId());
        ResourceHandler.getInstance().getUser((user, flag) -> {
            if (flag == FAILED) return;
            user.setInboxes(inboxes);
            ResourceHandler.getInstance().setUser(user, (usr, flg) -> {
                if (flag == FAILED) return;
                onUpdateInbox.onUpdateInbox(inbox, UPDATED);
            });
        });
        dRef.set(inbox)
                .addOnSuccessListener(aVoid -> {
                    onUpdateInbox.onUpdateInbox(inbox, UPDATED);
                })
                .addOnFailureListener(e -> {
                    onUpdateInbox.onUpdateInbox(null, FAILED);
                });
    }

    public interface OnUpdateInboxListener {
        public void onUpdateInbox(Inbox inbox, int flag);
    }


}
