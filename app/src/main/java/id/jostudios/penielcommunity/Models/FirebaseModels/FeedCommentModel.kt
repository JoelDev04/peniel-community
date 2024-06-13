package id.jostudios.penielcommunity.Models.FirebaseModels

data class FeedCommentModel(
    var id: Long = 0,
    var comments: MutableList<CommentModel> = mutableListOf()
);