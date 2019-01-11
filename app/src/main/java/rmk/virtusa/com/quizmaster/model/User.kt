package rmk.virtusa.com.quizmaster.model

data class User(
        var id: String,
        var name: String,
        var summary: String,
        var displayImage: String,
        var points: Int,
        var branchId: String,
        var chatIds: List<String>?) {
    constructor() : this("", "", "", "", 0, "", ArrayList<String>()) {}

}
