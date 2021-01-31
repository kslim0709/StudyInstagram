package com.kslim.studyinstagram.ui.navigation.model

data class AlarmDTO(
    var destinationUid: String? = null,
    var userId: String? = null,
    var uId: String? = null,
    // 0 : like alarm
    // 1 : comment alarm
    // 2 : follow alarm
    var kind: Int? = null,
    var message: String? = null,
    var timeStamp: Long? = null
)