package rmk.virtusa.com.quizmaster.model

import java.util.*
import kotlin.collections.ArrayList

data class Announcement(
        var firebaseUid: String,
        var title: String,
        var message: String,
        var attachments: List<String>,
        var announceDate: Date) {
    constructor() : this("", "", "", ArrayList<String>(), Date())
}
