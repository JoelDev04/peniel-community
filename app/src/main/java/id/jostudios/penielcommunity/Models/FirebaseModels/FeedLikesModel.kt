package id.jostudios.penielcommunity.Models.FirebaseModels

data class FeedLikesModel(
    var id: Long = 0,
    var userLikes: MutableList<String> = mutableListOf("100")
)