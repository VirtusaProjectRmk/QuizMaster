package rmk.virtusa.com.quizmaster.handler

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.greenrobot.eventbus.EventBus

object AnnouncementHandler {
    val annCollectionRef = FirebaseFirestore.getInstance().collection("announcements")

    init {

    }

    fun generateQuery(field: String, str: String): Query{
        return annCollectionRef.whereEqualTo("id", str)
    }


    fun getAnnouncements(query: Query) {
        query.get().addOnSuccessListener {
            EventBus.getDefault().post(null)
        }
    }
}