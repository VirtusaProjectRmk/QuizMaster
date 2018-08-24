package rmk.virtusa.com.quizmaster.handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

import rmk.virtusa.com.quizmaster.model.Branch;
import rmk.virtusa.com.quizmaster.model.Chat;
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

    public FirestoreList<Chat> getChats(String inboxId, FirestoreList.OnLoadListener<Chat> onLoadListener) {
        final FirestoreList<Chat> firestoreList = new FirestoreList<>(Chat.class, inboxCollectionRef.document(inboxId).collection("chats"), onLoadListener);
        return firestoreList;
    }

    public void getInboxes(OnUpdateInboxListener onUpdateInbox) {
        UserHandler.getInstance().getUser((user, flag) -> {
            if (user.getInboxes() == null) {
                onUpdateInbox.onUpdateInbox(null, EMPTY);
            } else {
                for (String inboxId : user.getInboxes()) {
                    inboxCollectionRef
                            .document(inboxId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Inbox inbox = documentSnapshot.toObject(Inbox.class);
                                if (inbox == null) {
                                    onUpdateInbox.onUpdateInbox(null, FAILED);
                                } else {
                                    onUpdateInbox.onUpdateInbox(inbox, UPDATED);
                                }
                            })
                            .addOnFailureListener(e -> {
                                onUpdateInbox.onUpdateInbox(null, FAILED);
                            });
                }
            }
        });
    }

    public void getInbox(String inboxId, OnUpdateInboxListener onUpdateInbox) {
        if (inboxId == null) {
            onUpdateInbox.onUpdateInbox(null, FAILED);
        } else if (inboxId.isEmpty()) {
            onUpdateInbox.onUpdateInbox(null, FAILED);
        } else {
            inboxCollectionRef.document(inboxId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Inbox inbox = documentSnapshot.toObject(Inbox.class);
                            onUpdateInbox.onUpdateInbox(inbox, UPDATED);
                        } else {
                            onUpdateInbox.onUpdateInbox(null, FAILED);
                        }
                    })
                    .addOnFailureListener(e -> {
                        onUpdateInbox.onUpdateInbox(null, FAILED);
                    });
        }
    }

    public void checkForInboxRedundancy(String userId1, OnUpdateInboxListener onUpdateInboxListener) {
        //TODO check if current user exist in userId2 inbox or vice-versa
        //return fail if does not exist
        //return update if user exist
        int flg = 0;
        getInboxes((inbox, flag) -> {
            switch (flag) {
                case UPDATED:
                    for (String userId : inbox.getUserIds()) {
                        if (userId.equals(userId1)) {
                            onUpdateInboxListener.onUpdateInbox(inbox, UPDATED);
                            break;
                        }
                    }
                    break;
                case FAILED:
                    onUpdateInboxListener.onUpdateInbox(null, FAILED);
                    break;
            }
        });
    }

    public void createInbox(String firebaseId, OnUpdateInboxListener onUpdateInbox) {
        List<String> users = new ArrayList<>();
        users.add(firebaseId);
        users.add(auth.getCurrentUser().getUid());
        checkForInboxRedundancy(firebaseId, (inb, flg) -> {
            if (flg == UPDATED) {
                Inbox inbox = new Inbox("", "", users);
                List<String> inboxes = new ArrayList<>();
                DocumentReference dRef = inboxCollectionRef.document();
                inboxes.add(dRef.getId());
                UserHandler.getInstance().getUser((user, flag) -> {
                    if (flag == FAILED) return;
                    user.setInboxes(inboxes);
                    UserHandler.getInstance().setUser(user, (usr, fl) -> {
                        if (fl == FAILED) return;
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
            } else onUpdateInbox.onUpdateInbox(inb, UPDATED);
        });
    }

    public void addChat(String inboxId, Chat chat, OnUpdateChatListener onUpdateChat) {
        CollectionReference chatCollectionRef = inboxCollectionRef.document(inboxId).collection("chats");
        chatCollectionRef
                .document()
                .set(chat, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    onUpdateChat.onUpdateChat(chat, UPDATED);
                })
                .addOnFailureListener(e -> {
                    onUpdateChat.onUpdateChat(null, FAILED);
                });
    }

    public interface OnUpdateInboxListener {
        public void onUpdateInbox(Inbox inbox, int flag);
    }


    public interface OnUpdateChatListener {
        public void onUpdateChat(Chat chat, int flag);
    }


}
