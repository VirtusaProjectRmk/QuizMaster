package rmk.virtusa.com.quizmaster.model

data class Link(
        var icon: String,
        var url: String) {
    constructor() : this("", "")
}
