package com.kslim.studyinstagram.data.repository

import com.google.firebase.auth.AuthCredential
import com.kslim.studyinstagram.data.firebase.FirebaseApi

class UserRepository {

    private val firebaseApi:FirebaseApi by lazy {
        FirebaseApi()
    }


    fun login(email: String, password: String) = firebaseApi.login(email, password)

    fun googleLogin(credential: AuthCredential) = firebaseApi.googleLogin(credential)

    fun facebookLogin(credential: AuthCredential) = firebaseApi.facebookLogin(credential)

    fun register(email: String, password: String) = firebaseApi.register(email, password)

    fun currentUser() = firebaseApi.currentUser()

    fun logout() = firebaseApi.logout()
}