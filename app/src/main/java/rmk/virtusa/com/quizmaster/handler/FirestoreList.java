package rmk.virtusa.com.quizmaster.handler;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

    public void add(T t, OnChangeListener<T> onChangeListener) {
        collectionReference.add(t)
                .addOnSuccessListener(documentReference -> {
                    put(t, documentReference.getId());
                    onChangeListener.onChange(t, true);
                })
                .addOnFailureListener(e -> onChangeListener.onChange(null, false));
    }

    /*
     * Adds to the list locally
     */
    public void add(T t) {
        put(t, "");
    }

    public void set(T t, OnChangeListener<T> onChangeListener) {
        String id = get(t);
        DocumentReference documentReference = id.isEmpty() ? collectionReference.document() : collectionReference.document(id);
        documentReference.set(t)
                .addOnSuccessListener(aVoid -> {
                    onChangeListener.onChange(t, true);
                })
                .addOnFailureListener(e -> {
                    onChangeListener.onChange(null, false);
                });
    }

    public Map.Entry<T, String> get(int i) {
        return (Entry<T, String>) entrySet().toArray()[i];
    }

    public void remove(T t, OnChangeListener<T> onChangeListener) {
        String id = get(t);
        if (id.isEmpty()) {
            remove(t);
            onChangeListener.onChange(null, false);
            return;
        }
        collectionReference.document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    remove(t);
                    onChangeListener.onChange(t, true);
                })
                .addOnFailureListener(e -> onChangeListener.onChange(null, false));
    }

    public interface OnChangeListener<T> {
        void onChange(T t, boolean didUpdate);
    }

    public interface onUpdate<T> {
        void onUpdate(T t, boolean didUpdate);
    }

    public interface OnAddListener<T> {
        void onAdd(T t, boolean didUpdate);
    }

    public interface OnRemoveListener<T> {
        void onRemove(boolean didUpdate);
    }

    public interface OnLoadListener<T> {
        void onLoad(boolean didLoad);
    }
}