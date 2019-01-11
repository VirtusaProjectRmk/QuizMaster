package rmk.virtusa.com.quizmaster.model

import java.util.*

data class Message(
        var senderUid: String,
        var isMedia: Boolean,
        var mediaType: Int,
        var chat: String,
        var sentTime: Date,
        var receivedTime: Date?) {
    constructor() : this("", false, 0, "", Date(), null)
}
