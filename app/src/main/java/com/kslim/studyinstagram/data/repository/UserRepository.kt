package com.kslim.studyinstagram.data.repository

import com.kslim.studyinstagram.data.firebase.FirebaseApi

class UserRepository(private val firebase: FirebaseApi) {

    fun login(email: String, password: String) = firebase.login(email, password)

    fun register(email: String, password: String) = firebase.register(email, password)

    fun currentUser() = firebase.currentUser()

    fun logout() = firebase.logout()
}