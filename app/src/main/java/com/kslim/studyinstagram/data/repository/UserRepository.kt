package com.kslim.studyinstagram.data.repository

import com.google.firebase.auth.AuthCredential
import com.kslim.studyinstagram.data.firebase.FirebaseApi

class UserRepository private constructor() {
    private var firebaseApi: FirebaseApi = FirebaseApi()

    companion object {
        @Volatile
        private var userRepository: UserRepository? = null

        fun getInstance(): UserRepository = userRepository ?: synchronized(this) {
            userRepository ?: UserRepository().also { userRepository = it }
        }
    }

    fun login(email: String, password: String) = firebaseApi.login(email, password)

    fun googleLogin(credential: AuthCredential) = firebaseApi.googleLogin(credential)

    fun facebookLogin(credential: AuthCredential) = firebaseApi.facebookLogin(credential)

    fun register(email: String, password: String) = firebaseApi.register(email, password)

    fun currentUser() = firebaseApi.currentUser()

    fun logout() = firebaseApi.logout()


    // Firebase Cloud data base
    fun requestFirebaseStoreItemList() = firebaseApi.requestFirebaseStoreItemList()
    fun updateFavoriteEvent(uId: String, imageUid: String) =
        firebaseApi.updateFavoriteEvent(uId, imageUid)

    fun requestFirebaseStoreUserItemList(uId: String) =
        firebaseApi.requestFirebaseStoreUserItemList(uId)

    fun getFirebaseStoreProfileImage(uId: String) = firebaseApi.getFirebaseStoreProfileImage(uId)
    fun requestFollow(uId: String, currentUid: String) = firebaseApi.requestFollow(uId, currentUid)
    fun getFollowerAndroidFollowing(uId: String) = firebaseApi.getFollowerAndroidFollowing(uId)
}