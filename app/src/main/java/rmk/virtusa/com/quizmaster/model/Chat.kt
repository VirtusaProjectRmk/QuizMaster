package rmk.virtusa.com.quizmaster.model

data class Chat(
        var chatId: String,
        var name: String,
        var dp: String,
        var status: String,
        var userIds: List<String>) {
    constructor() : this("", "", "", "", ArrayList<String>())
}
