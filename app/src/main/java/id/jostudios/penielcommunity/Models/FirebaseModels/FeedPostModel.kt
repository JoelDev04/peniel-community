package id.jostudios.penielcommunity.Models.FirebaseModels

import id.jostudios.penielcommunity.Enums.FeedType
import java.util.Date

data class FeedPostModel(
    var feedType: FeedType = FeedType.Thread,
    var caption: String = "",
    var totalLikes: Int = 0,

    var feedID: Long = 0,
    var feedOwnerID: String = "0",
    var uploadTime: Long = Date().time,

    var photoUrl: MutableList<String> = mutableListOf(""),
    var videoUrl: String = "",
    var thread: String = ""
)
