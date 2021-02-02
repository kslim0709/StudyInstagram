package com.kslim.studyinstagram.data.firebase

import android.net.Uri
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.kslim.studyinstagram.ui.navigation.model.AlarmDTO
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.ui.navigation.model.FollowDTO
import com.kslim.studyinstagram.utils.FcmPush
import io.reactivex.*
import java.util.*
import kotlin.collections.HashMap

class FirebaseFireStoreApi(private val firebaseAuthApi: FirebaseAuthApi) {

    private val firebaseStore: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val firebaseFireStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }


    // Firebase Storage
    fun requestFirebaseStoreItemList(): Flowable<HashMap<String, List<Any>>> {
        return Flowable.create({ emitter ->
            val reference = firebaseFireStore.collection("images").orderBy("timeStamp")
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
        val tsDoc = firebaseFireStore.collection("images").document(contentUid)
        firebaseFireStore.runTransaction { transition ->
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
        alarmDTO.userId = firebaseAuthApi.currentUser()?.email
        alarmDTO.uId = firebaseAuthApi.currentUser()?.uid
        alarmDTO.kind = 0
        alarmDTO.timeStamp = System.currentTimeMillis()
        firebaseFireStore.collection("alarms").document().set(alarmDTO)

        val message = firebaseAuthApi.currentUser()?.email + "좋아요"
        FcmPush.instance.sendMessage(destinationUid, "Howlstagram", message)
    }

    fun requestFirebaseStoreAllUserItemList(): Flowable<List<ContentDTO>> {
        return Flowable.create({ emitter ->
            val reference: Query =
                firebaseFireStore.collection("images")
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
                firebaseFireStore.collection("images").whereEqualTo("uid", uId)
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
            val reference = firebaseFireStore.collection("profileImages").document(uId)
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
        var tsDocFollowing = firebaseFireStore.collection("users").document(currentUserUid)
        firebaseFireStore.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uId] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction
            }

            if (followDTO.followings.containsKey(uId)) {
                //It remove following third person when a third person follow me
                followDTO.followingCount = followDTO.followingCount - 1
                followDTO.followings.remove(uId)
            } else {
                //It add following third person when a third person do not follow me
                followDTO.followingCount = followDTO.followingCount + 1
                followDTO.followings[uId] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }

        //Save data to third account

        var tsDocFollower = firebaseFireStore.collection("users").document(uId)
        firebaseFireStore.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid] = true
                followerAlarm(uId)
                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            if (followDTO!!.followers.containsKey(currentUserUid)) {
                //It cancel my follower when I follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid)
            } else {
                //It add my follower when I don't follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid] = true
                followerAlarm(uId)
            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }
    }

    fun followerAlarm(destinationUid: String) {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = firebaseAuthApi.currentUser()?.email
        alarmDTO.uId = firebaseAuthApi.currentUser()?.uid
        alarmDTO.kind = 2
        alarmDTO.timeStamp = System.currentTimeMillis()
        firebaseFireStore.collection("alarms").document().set(alarmDTO)

        val message = firebaseAuthApi.currentUser()?.email + "좋아요"
        FcmPush.instance.sendMessage(destinationUid, "HowIstagram", message)
    }

    fun getFollowerAndroidFollowing(uId: String): Flowable<DocumentSnapshot> {
        return Flowable.create({ emitter ->
            val reference: DocumentReference =
                firebaseFireStore.collection("users").document(uId)
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

    fun uploadContents(photoUri: Uri, photoName: String, photoExplain: String) =
        Completable.create { emitter ->

            val storageRef = firebaseStore.reference.child("images").child(photoName)

            // Promise method  ( 권장사항 )
            storageRef.putFile(photoUri)
                .continueWithTask {
                    return@continueWithTask storageRef.downloadUrl
                }.addOnSuccessListener { uri ->
                    val contentDTO = ContentDTO()
                    // Insert downloadUrl of image
                    contentDTO.imageUrl = uri.toString()

                    // Insert uid of user
                    contentDTO.uId = firebaseAuthApi.currentUser()?.uid

                    // Insert userId
                    contentDTO.userId = firebaseAuthApi.currentUser()?.email

                    // Insert explain of content
                    contentDTO.explain = photoExplain

                    //Insert TimeStamp
                    contentDTO.timeStamp = System.currentTimeMillis()

                    firebaseFireStore.collection("images").document().set(contentDTO)

                }.addOnCompleteListener {
                    if (!emitter.isDisposed) {
                        if (it.isSuccessful) {
                            emitter.onComplete()
                        } else {
                            emitter.onError(it.exception!!)
                        }
                    }
                }

            // Callback method
//            storageRef?.putFile(it)?.addOnSuccessListener {
//                Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
//                storageRef.downloadUrl.addOnCompleteListener { uri ->
//                    var contentDTO = ContentDTO()
//                    // Insert downloadUrl of image
//                    contentDTO.imageUrl = uri.toString()
//
//                    // Insert uid of user
//                    contentDTO.uId = auth?.currentUser?.uid
//
//                    // Insert userId
//                    contentDTO.userId = auth?.currentUser.email
//
//                    // Insert explain of content
//                    contentDTO.explain = addPhotoBinding.addphotoEditExplain.text.toString()
//
//                    //Insert TimeStamp
//                    contentDTO.timeStamp = System.currentTimeMillis()
//
//                    fireStore?.collection("images")?.document()?.set(contentDTO)
//                    setResult(Activity.RESULT_OK)
//
//                    finish()
//                }
//            }
        }

    fun requestFirebaseStoreUserAlarmList(uId: String): Flowable<List<AlarmDTO>> {
        return Flowable.create({ emitter ->
            val reference: Query =
                firebaseFireStore.collection("alarms").whereEqualTo("destinationUid", uId)
            val registration = reference.addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    emitter.onError(error)
                    return@addSnapshotListener
                }
                if (documentSnapshot != null) {
                    val itemList: ArrayList<AlarmDTO> = arrayListOf()
                    for (snapshot in documentSnapshot) {
                        val item = snapshot.toObject(AlarmDTO::class.java)
                        itemList.add(item)
                    }
                    emitter.onNext(itemList)
                }
            }
            emitter.setCancellable { registration.remove() }
        }, BackpressureStrategy.BUFFER)
    }

}