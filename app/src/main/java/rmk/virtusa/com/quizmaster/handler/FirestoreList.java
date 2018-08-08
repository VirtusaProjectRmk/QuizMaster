package rmk.virtusa.com.quizmaster.handler;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

//FIXME possible code smell
public class FirebaseQueue<T> extends HashMap<T, String> {
    private CollectionReference collectionReference;
    private Class<T> classType;

    public FirebaseQueue(Class<T> classType, CollectionReference collectionReference, OnChangeListener<T> onChangeListener) {
        this.collectionReference = collectionReference;
        this.classType = classType;
        collectionReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    onChangeListener.onChange(null, true);
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        put(documentSnapshot.toObject(this.classType), documentSnapshot.getId());
                    }
                })
                .addOnFailureListener(e -> {
                    onChangeListener.onChange(null, false);
                });
    }

    public void add(T t, OnChangeListener<T> onChangeListener) {
        collectionReference.add(t)
                .addOnSuccessListener(documentReference -> {
                    put(t, documentReference.getId());
                    onChangeListener.onChange(t, true);
                })
                .addOnFailureListener(e -> onChangeListener.onChange(null, false));
    }

    public void set(T t, OnChangeListener<T> onChangeListener) {
        String id = get(t);
        collectionReference.document(id)
                .set(t)
                .addOnSuccessListener(aVoid -> {
                    put(t, id);
                    onChangeListener.onChange(t, true);
                })
                .addOnFailureListener(e -> {
                    onChangeListener.onChange(null, false);
                });
    }

    public T get(int i) {
        Map.Entry<T, String> entry = (Map.Entry<T, String>) entrySet().toArray()[i];
        return entry.getKey();
    }

    public void remove(T t, OnChangeListener<T> onChangeListener) {
        String id = get(t);
        Log.i("", id);
        collectionReference.document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    remove(t);
                    onChangeListener.onChange(t, true);
                })
                .addOnFailureListener(e -> onChangeListener.onChange(null, false));
    }

    public interface OnChangeListener<T> {
        public void onChange(T t, boolean didUpdate);
    }
}