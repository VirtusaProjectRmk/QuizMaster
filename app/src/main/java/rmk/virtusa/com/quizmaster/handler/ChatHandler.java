package rmk.virtusa.com.quizmaster.handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;

import java.util.List;

import rmk.virtusa.com.quizmaster.model.Chat;

public class ChatHandler {
    public static final int UPDATED = 0;
    public static final int FAILED = 1;
    private static final ChatHandler ourInstance = new ChatHandler();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CollectionReference chatCollectionRef;

    private ChatHandler() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        chatCollectionRef = db.collection("chats");
    }

    public static ChatHandler getInstance() {
        return ourInstance;
    }

    public void addChat(String inboxId, Chat chat, OnUpdateChatListener onUpdateChat) {
        chatCollectionRef
                .document(inboxId)
                .collection(chat.getIsMedia() ? "media" : "text")
                .document()
                .set(chat, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    onUpdateChat.onUpdateChat(chat, UPDATED);
                })
                .addOnFailureListener(e -> {
                    onUpdateChat.onUpdateChat(null, FAILED);
                });
    }

    public void getChats(String inboxId, OnUpdateChatListener onUpdateChatListener) {
        chatCollectionRef
                .document(inboxId)
                .collection("text")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Chat> chats = queryDocumentSnapshots.toObjects(Chat.class);
                    for (Chat chat : chats) {
                        onUpdateChatListener.onUpdateChat(chat, UPDATED);
                    }
                })
                .addOnFailureListener(e -> {
                    onUpdateChatListener.onUpdateChat(null, FAILED);
                });
    }

    public interface OnUpdateChatListener {
        public void onUpdateChat(Chat chat, int flag);
    }
}
