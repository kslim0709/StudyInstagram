package com.kslim.studyinstagram.ui.navigation.model

class ContentDTO(
    var explain: String? = null,
    var imageUrl: String? = null,
    var uId: String? = null,
    var userId: String? = null,
    var timeStamp: Long? = null,
    var favoriteCount: Int = 0,
    var favorites: MutableMap<String, Boolean> = HashMap(),
    var imageUid: String? = null
) {
    data class Comment(
        var uId: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timeStamp: Long? = null
    )
}