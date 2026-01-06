package hu.bme.ait.clubhub.data

data class Post(
    var uid: String = "",
    var author: String = "",
    var postDate: String = "",
    var postTitle: String = "",
    var postBody: String = "",
    var meetingTimes: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null
)

data class PostWithId(
    var postId: String = "",
    var post: Post
)