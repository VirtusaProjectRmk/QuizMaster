package rmk.virtusa.com.quizmaster.handler;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

//FIXME Class is a possible code smell
public class FirestoreList<T> extends HashMap<T, String> {
    boolean isLoaded = false;
    private CollectionReference collectionReference;
    private Class<T> classType;
    private OnLoadListener<T> onLoadListener;

    public FirestoreList(Class<T> classType, CollectionReference collectionReference, OnLoadListener<T> onLoadListener) {
        this.classType = classType;
        this.collectionReference = collectionReference;
        this.onLoadListener = onLoadListener;
        execute(onLoadListener);
    }

    void execute(OnLoadListener<T> onLoadListener) {
        collectionReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        put(documentSnapshot.toObject(this.classType), documentSnapshot.getId());
                    }
                    //Denotes initialization of FirestoreList
                    if (onLoadListener != null) {
                        onLoadListener.onLoad(true);
                    }
                })
                .addOnFailureListener(e -> {
                    if (onLoadListener != null) {
                        onLoadListener.onLoad(false);
                    }
                });
    }

    public void setOnLoadListener(OnLoadListener<T> onLoadListener) {
        this.onLoadListener = onLoadListener;
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

    public interface OnLoadListener<T> {
        public void onLoad(boolean didLoad);
    }
}