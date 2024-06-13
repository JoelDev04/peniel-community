package id.jostudios.penielcommunity.Models.FirebaseModels

data class CommentModel(
    var commentID: Int,
    var content: String,
    var userID: Int,
    var date: Long
)
