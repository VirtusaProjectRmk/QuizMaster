package rmk.virtusa.com.quizmaster.handler;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import rmk.virtusa.com.quizmaster.model.Chat;
import rmk.virtusa.com.quizmaster.model.Message;
import rmk.virtusa.com.quizmaster.model.User;

public class ChatHandler {
    public static final int UPDATED = 0;
    public static final int FAILED = 1;
    public static final int EMPTY = 2;
    private static final ChatHandler ourInstance = new ChatHandler();
    private static final String TAG = "ChatHandler";

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CollectionReference chatCollectionRef;

    private ChatHandler() {

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        chatCollectionRef = db.collection("chats");
    }

    public static ChatHandler getInstance() {
        return ourInstance;
    }

    public CollectionReference getMessagesCollectionRef(String chatId) {
        return chatCollectionRef.document(chatId).collection("messages");
    }

    public void getMessages(String chatId) {
        getMessagesCollectionRef(chatId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            EventBus.getDefault().post(queryDocumentSnapshots.toObjects(Message.class));
        }).addOnFailureListener(e -> {
            EventBus.getDefault().post(new ArrayList<>());
        });
    }

    public void getChats(User user) {
        if (user.getChatIds() == null) {
            EventBus.getDefault().post(new Chat());
        } else {
            for (String chatId : user.getChatIds()) {
                chatCollectionRef
                        .document(chatId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            if (chat == null) {
                                EventBus.getDefault().post(new Chat());
                            } else {
                                EventBus.getDefault().post(chat);
                            }
                        })
                        .addOnFailureListener(e -> {
                            EventBus.getDefault().post(new Chat());
                        });
            }
        }
    }

    public void getChat(String chatId) {
        if (chatId == null || chatId.isEmpty()) {
            EventBus.getDefault().post(null);
        } else {
            chatCollectionRef.document(chatId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            EventBus.getDefault().post(chat);
                        } else {
                            EventBus.getDefault().post(new Chat());
                        }
                    })
                    .addOnFailureListener(e -> {
                        EventBus.getDefault().post(new Chat());
                    });
        }
    }


    public void createInbox(String firebaseId) {
        List<String> users = new ArrayList<>();
        users.add(firebaseId);
        users.add(auth.getCurrentUser().getUid());
//        checkForInboxRedundancy(firebaseId, (inb, flg) -> {
//            if (flg == UPDATED) {
//                Chat inbox = new Chat("", "", users);
//                List<String> inboxes = new ArrayList<>();
//                DocumentReference dRef = chatCollectionRef.document();
//                inboxes.add(dRef.getId());
//                UserHandler.getInstance().getUser((user, flag) -> {
//                    if (flag == FAILED) return;
//                    user.setChatIds(inboxes);
//                    UserHandler.getInstance().setUser(user, (usr, fl) -> {
//                        if (fl == FAILED) return;
//                        onUpdateInbox.onUpdateInbox(inbox, UPDATED);
//                    });
//                });
//                dRef.set(inbox)
//                        .addOnSuccessListener(aVoid -> {
//                            onUpdateInbox.onUpdateInbox(inbox, UPDATED);
//                        })
//                        .addOnFailureListener(e -> {
//                            onUpdateInbox.onUpdateInbox(null, FAILED);
//                        });
//            } else onUpdateInbox.onUpdateInbox(inb, UPDATED);
//        });
    }


}
