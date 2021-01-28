package com.kslim.studyinstagram.data.repository

import com.google.firebase.auth.AuthCredential
import com.kslim.studyinstagram.data.firebase.FirebaseAuthApi
import com.kslim.studyinstagram.data.firebase.FirebaseFireStoreApi

class UserRepository private constructor() {
    private var firebaseApi: FirebaseAuthApi = FirebaseAuthApi()
    private var firebaseFireStoreApi: FirebaseFireStoreApi = FirebaseFireStoreApi()

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
    fun requestFirebaseStoreItemList() = firebaseFireStoreApi.requestFirebaseStoreItemList()
    fun updateFavoriteEvent(uId: String, imageUid: String) =
        firebaseFireStoreApi.updateFavoriteEvent(uId, imageUid)

    fun requestFirebaseStoreAllUserItemList() = firebaseFireStoreApi.requestFirebaseStoreAllUserItemList()

    fun requestFirebaseStoreUserItemList(uId: String) =
        firebaseFireStoreApi.requestFirebaseStoreUserItemList(uId)

    fun getFirebaseStoreProfileImage(uId: String) = firebaseFireStoreApi.getFirebaseStoreProfileImage(uId)
    fun requestFollow(uId: String, currentUid: String) = firebaseFireStoreApi.requestFollow(uId, currentUid)
    fun getFollowerAndroidFollowing(uId: String) = firebaseFireStoreApi.getFollowerAndroidFollowing(uId)
}