package rmk.virtusa.com.quizmaster.model

import java.util.Date

data class QuizMetadata(
        var attended: Int,
        var answeredCorrectly: Int,
        var multiplier: Int,
        var dateTaken: Date?) {
    constructor() : this(0, 0, 0, Date())
}
