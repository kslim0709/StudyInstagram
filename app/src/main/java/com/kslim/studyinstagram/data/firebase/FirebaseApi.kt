package com.kslim.studyinstagram.data.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.ui.navigation.model.FollowDTO
import io.reactivex.*


class FirebaseApi {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firebaseStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun login(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                emitter.onComplete()
            } else {
                emitter.onError(it.exception!!)
            }
        }
    }

    fun googleLogin(credential: AuthCredential) = Completable.create { emitter ->
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (!emitter.isDisposed) {
                emitter.onComplete()
            } else {
                emitter.onError(it.exception!!)
            }
        }
    }

    fun facebookLogin(credential: AuthCredential) = Completable.create { emitter ->
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (!emitter.isDisposed) {
                emitter.onComplete()
            } else {
                emitter.onError(it.exception!!)
            }
        }
    }

    fun register(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser


    // Firebase Storage
    fun requestFirebaseStoreItemList() = Single.create<List<ContentDTO>> { emitter ->
        firebaseStore.collection("images").orderBy("timeStamp")
            .addSnapshotListener { values, error ->
                if (error != null) {
                    emitter.onError(error)
                    return@addSnapshotListener
                }

                val itemList: ArrayList<ContentDTO> = arrayListOf()
                for (value in values!!.documents) {
                    val item = value.toObject(ContentDTO::class.java)
                    item?.imageUid = value.id
                    itemList.add(item!!)

                }
                emitter.onSuccess(itemList)

            }
    }

    fun updateFavoriteEvent(uId: String, imageUid: String) = Completable.create { emitter ->
        val tsDoc = firebaseStore.collection("images").document(imageUid)
        firebaseStore.runTransaction { transition ->
            val contentDTO = transition.get(tsDoc).toObject(ContentDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uId)) {
                // When the button is clicked
                contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                contentDTO.favorites.remove(uId)

            } else {
                // when the button is not clicked
                contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                contentDTO.favorites[uId] = true
            }

            transition.set(tsDoc, contentDTO)
            return@runTransaction
        }.addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(it.exception!!)
                }
            }
        }
    }

    fun requestFirebaseStoreUserItemList(uId: String) = Single.create<List<ContentDTO>> { emitter ->
        firebaseStore.collection("images").whereEqualTo("uid", uId)
            .addSnapshotListener { values, error ->
                // Sometimes, This code return null of querySnapshot when it sign out
                if (error != null) {
                    emitter.onError(error)
                    return@addSnapshotListener
                }

                //Get Data
                val itemList: ArrayList<ContentDTO> = arrayListOf()
                for (value in values!!.documents) {
                    val item = value.toObject(ContentDTO::class.java)
                    itemList.add(item!!)
                }
                emitter.onSuccess(itemList)

            }
    }

    fun getFirebaseStoreProfileImage(uId: String) = Single.create<DocumentSnapshot> { emitter ->
        firebaseStore.collection("profileImages").document(uId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    emitter.onError(error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    emitter.onSuccess(value)
                } else {
                    emitter.onError(Throwable("Data is null"))
                }
            }
    }

    fun requestFollow(uId: String, currentUserUid: String) = Completable.create { emitter ->

        // Save data to my account
        val tsDocFollowing = firebaseStore.collection("users").document(currentUserUid)
        firebaseStore.runTransaction { transition ->
            var followDTO = transition.get(tsDocFollowing).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followers[uId] = true

                transition.set(tsDocFollowing, followDTO)
                return@runTransaction
            }

            if (followDTO.followers.containsKey(uId)) {
                // It remove following third person when a third person follow me
                followDTO.followingCount = followDTO.followingCount - 1
                followDTO.followers.remove(uId)
            } else {
                //It add following third person when a third person do not follow me
                followDTO.followingCount = followDTO.followingCount + 1
                followDTO.followers[uId] = true
            }
            transition.set(tsDocFollowing, followDTO)
            return@runTransaction
        }
        // Save Data to third person
        val tsDocFollower = firebaseStore.collection("users").document(uId)
        firebaseStore.runTransaction { transition ->
            var followerDTO = transition.get(tsDocFollower).toObject(FollowDTO::class.java)
            if (followerDTO == null) {
                followerDTO = FollowDTO()
                followerDTO.followerCount = 1
                followerDTO.followers[currentUserUid] = true

                transition.set(tsDocFollower, followerDTO)
                return@runTransaction
            }

            if (followerDTO.followers.containsKey(currentUserUid)) {
                // It cancel my follower when I follow a third person
                followerDTO.followerCount = followerDTO.followerCount - 1
                followerDTO.followers.remove(currentUserUid)
            } else {
                // It add my follower when I don't follow a third person
                followerDTO.followerCount = followerDTO.followerCount + 1
                followerDTO.followers[currentUserUid]
            }
            transition.set(tsDocFollower, followerDTO)
            return@runTransaction
        }
    }

    fun getFollowerAndroidFollowing(uId: String) =
        Single.create<DocumentSnapshot> { emitter ->
            firebaseStore.collection("users").document(uId)
                .addSnapshotListener { documentSnapshot, exception ->
                    if (exception != null) {
                        emitter.onError(exception)
                        return@addSnapshotListener
                    }
                    if (documentSnapshot == null) {
                        emitter.onError(Throwable("Data is null"))
                        return@addSnapshotListener
                    }

                    emitter.onSuccess(documentSnapshot)
                }
        }

}
