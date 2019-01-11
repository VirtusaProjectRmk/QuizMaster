package rmk.virtusa.com.quizmaster.model

data class Report(
        var reportedUid: String,
        var comments: String,
        var reportedBy: String) {
    constructor() : this("", "", "")
}
