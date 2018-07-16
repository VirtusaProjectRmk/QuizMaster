package rmk.virtusa.com.quizmaster.handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import rmk.virtusa.com.quizmaster.model.Chat;

class ChatHandler {
    public static final int PUSHED = 0;
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

    static ChatHandler getInstance() {
        return ourInstance;
    }

    public void getChats(String chatId, OnUpdateChatListener onUpdateChatListener) {
        chatCollectionRef
                .document(chatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Chat chat = documentSnapshot.toObject(Chat.class);
                    onUpdateChatListener.onUpdateChat(chat, PUSHED);
                })
                .addOnFailureListener(e -> {

                });
    }

    public interface OnUpdateChatListener {
        public void onUpdateChat(Chat chat, int flag);
    }
}
