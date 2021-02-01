package com.kslim.studyinstagram.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kslim.studyinstagram.ui.navigation.model.PushDTO
import com.squareup.okhttp.*
import java.io.IOException

class FcmPush {
    var JSON: MediaType = MediaType.parse("application/json; charset=utf-8")
    var URL = "https://fcm.googleapis.com/fcm/send"
    var serverKey =
        "AAAA82TYrts:APA91bFpTSvMc5C24RE-wdxKcTAJoHe0LV6xpv2HNU2DNKuVxNmVVPs0gckaiFRtzYXW0CB2fcdQfXgUVEMroSrKQQ06CFRNWakHdAcS1D10W2NRLjRM_26ToliYWH4XMfJJL5s4dtE4"
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

                    val pushMessage = gson?.toJson(pushDTO)

                    var request =
                        Request.Builder().url(URL).addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "key=" + serverKey)
                            .post(RequestBody.create(JSON, pushMessage)).build()

                    okHttpClient?.newCall(request)?.enqueue(object : Callback {
                        override fun onFailure(request: Request?, e: IOException?) {
                            Log.e("FCMPush", "onFailure: ${e?.message}")
                        }

                        override fun onResponse(response: Response?) {
                            Log.v("FCMPush", response?.body().toString())
                        }

                    })

                }
            }
    }


}
