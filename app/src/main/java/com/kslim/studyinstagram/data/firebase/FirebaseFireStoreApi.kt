package com.kslim.studyinstagram.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.kslim.studyinstagram.ui.navigation.model.AlarmDTO
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.ui.navigation.model.FollowDTO
import com.kslim.studyinstagram.utils.FcmPush
import io.reactivex.*

class FirebaseFireStoreApi {
    private val firebaseStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }


    // Firebase Storage
    fun requestFirebaseStoreItemList(): Flowable<HashMap<String, List<Any>>> {
        return Flowable.create({ emitter ->
            val reference = firebaseStore.collection("images").orderBy("timeStamp")
            val registration = reference.addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    emitter.onError(error)
                }
                if (documentSnapshot != null) {
                    val dataMap: HashMap<String, List<Any>> = HashMap()
                    val itemList: ArrayList<ContentDTO> = arrayListOf()
                    val contentUidList: ArrayList<String> = arrayListOf()
                    for (value in documentSnapshot.documents) {
                        val item = value.toObject(ContentDTO::class.java)
                        itemList.add(item!!)
                        contentUidList.add(value.id)
                    }
                    dataMap["item"] = itemList
                    dataMap["contentUid"] = contentUidList
                    emitter.onNext(dataMap)
                }
            }
            emitter.setCancellable { registration.remove() }
        }, BackpressureStrategy.BUFFER)
    }


    fun updateFavoriteEvent(uId: String, contentUid: String) = Completable.create { emitter ->
        val tsDoc = firebaseStore.collection("images").document(contentUid)
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
        firebaseStore.collection("alarms").document().set(alarmDTO)

        val message = FirebaseAuth.getInstance().currentUser?.email + "좋아요"
        FcmPush.instance.sendMessage(destinationUid, "Howlstagram", message)
    }

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

    fun getFirebaseStoreProfileImage(uId: String): Flowable<DocumentSnapshot> {
        return Flowable.create({ emitter ->
            val reference = firebaseStore.collection("profileImages").document(uId)
            val registration = reference.addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    emitter.onError(error)
                    return@addSnapshotListener
                }
                if (documentSnapshot != null) {
                    emitter.onNext(documentSnapshot)
                }
            }
            emitter.setCancellable { registration.remove() }
        }, BackpressureStrategy.BUFFER)
    }

    fun requestFollow(uId: String, currentUserUid: String) = Completable.create { emitter ->

        //Save data to my account
        var tsDocFollowing = firebaseStore.collection("users").document(currentUserUid)
        firebaseStore.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uId] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction
            }

            if(followDTO.followings.containsKey(uId)){
                //It remove following third person when a third person follow me
                followDTO.followingCount = followDTO.followingCount - 1
                followDTO.followings.remove(uId)
            }else{
                //It add following third person when a third person do not follow me
                followDTO.followingCount = followDTO.followingCount + 1
                followDTO.followings[uId] = true
            }
            transaction.set(tsDocFollowing,followDTO)
            return@runTransaction
        }

        //Save data to third account

        var tsDocFollower = firebaseStore.collection("users").document(uId)
        firebaseStore.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid] = true
                followerAlarm(uId)
                transaction.set(tsDocFollower,followDTO!!)
                return@runTransaction
            }

            if(followDTO!!.followers.containsKey(currentUserUid)){
                //It cancel my follower when I follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid)
            }else{
                //It add my follower when I don't follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid] = true
                followerAlarm(uId)
            }
            transaction.set(tsDocFollower,followDTO!!)
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
        firebaseStore.collection("alarms").document().set(alarmDTO)

        val message = FirebaseAuth.getInstance().currentUser?.email + "좋아요"
        FcmPush.instance.sendMessage(destinationUid, "HowIstagram", message)
    }

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