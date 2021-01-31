package com.kslim.studyinstagram.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kslim.studyinstagram.ui.navigation.model.PushDTO
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class FcmPush {
    var JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    var URL = "https://fcm.googleapis.com/v1/projects/myproject-b5ae1/messages:send"
    var serverKey = ""
    var gson: Gson? = null
    var okHttpClient: OkHttpClient? = null

    companion object {
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid: String, title: String, message: String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var token = task.result?.get("pushToken").toString()

                    var pushDTO = PushDTO()
                    pushDTO.to = token
                    pushDTO.notification.title = title
                    pushDTO.notification.body = message

                    var body = RequestBody.create(JSON, gson?.toJson(pushDTO)!!)
                    var request = Request.Builder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "key=" + serverKey)
                        .url(URL)
                        .post(body)
                        .build()

                    okHttpClient?.newCall(request)?.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            TODO("Not yet implemented")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            println(response.body?.string())
                        }

                    })

                }
            }
    }


}
