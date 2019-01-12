package rmk.virtusa.com.quizmaster.handler;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreList<T> extends HashMap<T, String> {
    private static final String TAG = "FirestoreList";
    boolean isLoaded = false;
    private CollectionReference collectionReference;
    private Class<T> classType;
    private OnLoadListener<T> onLoadListener;
    private OnAddListener<T> onAddListener;
    private OnRemoveListener<T> onRemoveListener;
    private OnModifiedListener<T> onModifiedListener;

    public FirestoreList(Class<T> classType, CollectionReference collectionReference, OnLoadListener<T> onLoadListener) {
        this.classType = classType;
        this.collectionReference = collectionReference;
        this.onLoadListener = onLoadListener;
        execute(onLoadListener);

    }

    public void setOnAddListener(OnAddListener<T> onAddListener) {
        this.onAddListener = onAddListener;
    }

    public void setOnRemoveListener(OnRemoveListener<T> onRemoveListener) {
        this.onRemoveListener = onRemoveListener;
    }

    public void setOnModifiedListener(OnModifiedListener<T> onModifiedListener) {
        this.onModifiedListener = onModifiedListener;
    }

    void execute(OnLoadListener<T> onLoadListener) {
        collectionReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        put(documentSnapshot.toObject(this.classType), documentSnapshot.getId());
                    }
                    //Denotes initialization of FirestoreList
                    if (onLoadListener != null) {
                        onLoadListener.onLoad();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Cannot fetch details");
                });
    }


    /*
     * Tries to push and updates to the list locally after succesfully completed
     */
    public void add(T t) {
        add(t, onAddListener);
    }

    public void addLocally(T t, String val) {
        put(t, val);
    }

    public void add(T t, OnAddListener<T> onAddListener) {
        collectionReference.add(t)
                .addOnSuccessListener(documentReference -> {
                    put(t, documentReference.getId());
                    if (onAddListener != null) {
                        onAddListener.onAdd(t);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Cannot add to db"));
    }


    public void set(T t) {
        set(t, onModifiedListener);
    }

    public void set(T t, OnModifiedListener<T> onModifiedListener) {
        String id = get(t);
        DocumentReference documentReference = id == null || id.isEmpty() ? collectionReference.document() : collectionReference.document(id);
        documentReference.set(t)
                .addOnSuccessListener(aVoid -> {
                    if (onModifiedListener != null) {
                        onModifiedListener.onModified(t);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Modifiy failed");
                });
    }

    public Map.Entry<T, String> get(int i) {
        return (Entry<T, String>) entrySet().toArray()[i];
    }

    @Override
    public String remove(Object o) {
        return remove(o, onRemoveListener);
    }

    public String remove(Object o, OnRemoveListener<T> onRemoveListener) {
        String id = get(o);
        if (id == null || id.isEmpty()) {
            //remove(t);
            //onChangeListener.onChange(null, false);
            return id;
        }
        collectionReference.document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    super.remove(o);
                    if (onRemoveListener != null) {
                        onRemoveListener.onRemove((T) o);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Cannot remove from list"));
        return id;
    }

    public interface OnAddListener<T> {
        void onAdd(T t);
    }

    public interface OnRemoveListener<T> {
        void onRemove(T t);
    }

    public interface OnModifiedListener<T> {
        void onModified(T t);
    }

    public interface OnFailListener<T> {
        void onFail();
    }

    public interface OnLoadListener<T> {
        void onLoad();
    }
}