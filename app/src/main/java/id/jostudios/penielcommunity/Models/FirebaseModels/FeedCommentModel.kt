package id.jostudios.penielcommunity.Models.FirebaseModels

data class FeedCommentModel(
    var feedID: Long = 0,
    var comments: MutableList<CommentModel> = mutableListOf()
);