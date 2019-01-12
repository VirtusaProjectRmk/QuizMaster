package rmk.virtusa.com.quizmaster.handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

//T - any POJO
public class UserUpdater<T> {
    T t;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    protected UserUpdater() {
    }

    public UserUpdater(String ref) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public T get() {
        return t;
    }

    public UserUpdater<T> set(T t) {
        this.t = t;
        return this;
    }

    public void update(OnUpdateListener<T> onUpdate) {
        documentReference.set(t)
                .addOnSuccessListener(aVoid -> {
                    onUpdate.onUpdate(t, true);
                })
                .addOnFailureListener(e -> {
                    onUpdate.onUpdate(t, false);
                });
    }

    public interface OnUpdateListener<T> {
        void onUpdate(T t, boolean didUpdate);
    }
}
