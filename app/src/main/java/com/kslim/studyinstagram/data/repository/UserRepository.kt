package com.kslim.studyinstagram.data.repository

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.kslim.studyinstagram.data.firebase.FirebaseAuthApi
import com.kslim.studyinstagram.data.firebase.FirebaseFireStoreApi

class UserRepository private constructor() {


    companion object {
        @Volatile
        private var userRepository: UserRepository? = null

        fun getInstance(): UserRepository = userRepository ?: synchronized(this) {
            userRepository ?: UserRepository().also { userRepository = it }
        }
    }

    private var firebaseApi = FirebaseAuthApi()
    private var firebaseFireStoreApi = FirebaseFireStoreApi(firebaseApi)


    fun signInAndSignUp(email: String, password: String) =
        firebaseApi.signInAndSignUp(email, password)

    fun googleLogin(credential: AuthCredential) = firebaseApi.googleLogin(credential)

    fun facebookLogin(credential: AuthCredential) = firebaseApi.facebookLogin(credential)

    fun register(email: String, password: String) = firebaseApi.register(email, password)

    fun currentUser() = firebaseApi.currentUser()

    fun logout() = firebaseApi.logout()


    // Firebase Cloud data base
    fun requestFirebaseStoreItemList() = firebaseFireStoreApi.requestFirebaseStoreItemList()
    fun updateFavoriteEvent(uId: String, imageUid: String) =
        firebaseFireStoreApi.updateFavoriteEvent(uId, imageUid)

    fun requestFirebaseStoreAllUserItemList() =
        firebaseFireStoreApi.requestFirebaseStoreAllUserItemList()

    fun requestFirebaseStoreUserItemList(uId: String) =
        firebaseFireStoreApi.requestFirebaseStoreUserItemList(uId)

    fun getFirebaseStoreProfileImage(uId: String) =
        firebaseFireStoreApi.getFirebaseStoreProfileImage(uId)

    fun requestFollow(uId: String, currentUid: String) =
        firebaseFireStoreApi.requestFollow(uId, currentUid)

    fun getFollowerAndroidFollowing(uId: String) =
        firebaseFireStoreApi.getFollowerAndroidFollowing(uId)

    fun uploadContents(photoUri: Uri, photoName: String, photoExplain: String) =
        firebaseFireStoreApi.uploadContents(photoUri, photoName, photoExplain)

    fun requestFirebaseStoreUserAlarmList(uId: String) =
        firebaseFireStoreApi.requestFirebaseStoreUserAlarmList(uId)
}