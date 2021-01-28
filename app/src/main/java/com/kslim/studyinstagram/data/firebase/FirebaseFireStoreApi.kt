package com.kslim.studyinstagram.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.kslim.studyinstagram.ui.navigation.model.AlarmDTO
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.ui.navigation.model.FollowDTO
import io.reactivex.*

class FirebaseFireStoreApi {
    private val firebaseStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }


    // Firebase Storage
    fun requestFirebaseStoreItemList() = Single.create<HashMap<String, List<Any>>> { emitter ->
        firebaseStore.collection("images").orderBy("timeStamp")
            .addSnapshotListener { values, error ->
                if (error != null) {
                    emitter.onError(error)
                    return@addSnapshotListener
                }
                Log.v("Firebase", "requestFirebaseStoreItemList ${values}")
                val itemList: ArrayList<ContentDTO> = arrayListOf()
                val contentUidList: ArrayList<String> = arrayListOf()
                val dataMap: HashMap<String, List<Any>> = HashMap()
                for (value in values!!.documents) {
                    val item = value.toObject(ContentDTO::class.java)
                    itemList.add(item!!)
                    contentUidList.add(value.id)

                }
                dataMap["item"] = itemList
                dataMap["contentUid"] = contentUidList
                emitter.onSuccess(dataMap)

            }
    }

//    fun requestFirebaseStoreItemList() : Task<QuerySnapshot> {
//        return firebaseStore.collection("images").orderBy("timeStamp").get()
//    }


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
                favoriteAlarm(contentDTO.uId!!)
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


    fun favoriteAlarm(destinationUid: String) {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uId = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind = 0
        alarmDTO.timeStamp = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
    }

//
//    fun requestFirebaseStoreUserItemList(uId: String) = Single.create<List<ContentDTO>> { emitter ->
//        firebaseStore.collection("images").whereEqualTo("uid", uId)
//            .addSnapshotListener { values, error ->
//                // Sometimes, This code return null of querySnapshot when it sign out
//                if (error != null) {
//                    emitter.onError(error)
//                    return@addSnapshotListener
//                }
//                //Get Data
//                val itemList: ArrayList<ContentDTO> = arrayListOf()
//                for (value in values!!.documents) {
//                    val item = value.toObject(ContentDTO::class.java)
//                    itemList.add(item!!)
//                }
//                emitter.onSuccess(itemList)
//
//            }
//    }

    fun requestFirebaseStoreAllUserItemList(): Flowable<List<ContentDTO>> {
        return Flowable.create({ emitter ->
            val reference: Query =
                firebaseStore.collection("images")
            val registration = reference.addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    emitter.onError(e)
                }
                if (documentSnapshot != null) {
                    val itemList: ArrayList<ContentDTO> = arrayListOf()
                    for (value in documentSnapshot.documents) {
                        val item = value.toObject(ContentDTO::class.java)
                        itemList.add(item!!)
                    }
                    emitter.onNext(itemList)
                }
            }
            emitter.setCancellable { registration.remove() }
        }, BackpressureStrategy.BUFFER)
    }

    fun requestFirebaseStoreUserItemList(uId: String?): Flowable<List<ContentDTO>> {
        return Flowable.create({ emitter ->
            val reference: Query =
                firebaseStore.collection("images").whereEqualTo("uid", uId)
            val registration = reference.addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    emitter.onError(e)
                }
                if (documentSnapshot != null) {
                    val itemList: ArrayList<ContentDTO> = arrayListOf()
                    for (value in documentSnapshot.documents) {
                        val item = value.toObject(ContentDTO::class.java)
                        itemList.add(item!!)
                    }
                    emitter.onNext(itemList)
                }
            }
            emitter.setCancellable { registration.remove() }
        }, BackpressureStrategy.BUFFER)
    }

    fun getFirebaseStoreProfileImage(uId: String) = Single.create<DocumentSnapshot> { emitter ->
        firebaseStore.collection("profileImages").document(uId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    emitter.onError(error)
                    return@addSnapshotListener
                }
                Log.v("Firebase", "profileImage ${value}")
                if (value != null) {
                    Log.v("Firebase", "onSuccess profileImage ${value}")
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

            if (followDTO.followings.containsKey(uId)) {
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
                followerAlarm(uId)
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
                followerAlarm(uId)
            }
            transition.set(tsDocFollower, followerDTO)
            return@runTransaction
        }
    }

    fun followerAlarm(destinationUid: String) {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uId = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind = 2
        alarmDTO.timeStamp = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
    }

//    fun getFollowerAndroidFollowing(uId: String) =
//        Single.create<DocumentSnapshot> { emitter ->
//            firebaseStore.collection("users").document(uId)
//                .addSnapshotListener { documentSnapshot, exception ->
//                    if (exception != null) {
//                        emitter.onError(exception)
//                        return@addSnapshotListener
//                    }
//                    if (documentSnapshot == null) {
//                        emitter.onError(Throwable("Data is null"))
//                        return@addSnapshotListener
//                    }
//
//                    emitter.onSuccess(documentSnapshot)
//                }
//        }


    fun getFollowerAndroidFollowing(uId: String): Flowable<DocumentSnapshot> {
        return Flowable.create({ emitter ->
            val reference: DocumentReference =
                firebaseStore.collection("users").document(uId)
            val registration = reference.addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    emitter.onError(e)
                }
                if (documentSnapshot != null) {
                    emitter.onNext(documentSnapshot)
                }
            }
            emitter.setCancellable { registration.remove() }
        }, BackpressureStrategy.BUFFER)
    }
}