package rmk.virtusa.com.quizmaster.model

data class Branch(
        var name: String,
        var moto: String,
        var admins: List<String>? = null) {
    constructor() : this("", "", ArrayList())
}
