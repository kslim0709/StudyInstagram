package com.kslim.studyinstagram.ui.navigation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.ui.navigation.model.FollowDTO
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val contentDTOs: MutableLiveData<List<ContentDTO>> = MutableLiveData()
    private val profileURL: MutableLiveData<String> = MutableLiveData()
    private val followDTO: MutableLiveData<FollowDTO> = MutableLiveData()

    fun requestFirebaseStoreItemList(uId: String) {
        repository.requestFirebaseStoreUserItemList(uId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).toObservable()
            .subscribe(object : Observer<List<ContentDTO>> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: List<ContentDTO>) {
                    Log.e("UserFragment", "requestFirebaseStoreItemList onNext:${t.toString()} ")
                    contentDTOs.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e("UserFragment", "requestFirebaseStoreItemList exception: " + e.message)
                }

                override fun onComplete() {

                }
            })

//        val disposable = repository.requestFirebaseStoreUserItemList(uId)
//            .subscribeOn(Schedulers.io())
//            .subscribe({
//                contentDTOs.value = it
//            }, {
//                Log.e("UserFragment", "requestFirebaseStoreItemList exception: " + it.message)
//            })
//
//        disposables.add(disposable)
    }

    fun getFirebaseStoreProfileImage(uId: String) {
        val disposable = repository.getFirebaseStoreProfileImage(uId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                Log.v("UserViewModel", "ProfileImage ${it.data}")

                if (it.data != null) {
                    val url = it.data!!["image"]
                    profileURL.value = url as String
                }
            }, {
                Log.e("UserFragment", "getFirebaseStoreProfileImage exception: " + it.message)
            })
        disposables.add(disposable)
    }

    fun requestFollow(uId: String, currentUid: String) {
        val disposable = repository.requestFollow(uId, currentUid).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe()

        disposables.add(disposable)
    }

    fun getFollowerAndroidFollowing(uId: String) {
        repository.getFollowerAndroidFollowing(uId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
            .subscribe(object : Observer<DocumentSnapshot> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: DocumentSnapshot) {
                    Log.v("UserFragment", "getFollowerAndroidFollowing exception: ${t.toObject(FollowDTO::class.java)}")
                    followDTO.value = t.toObject(FollowDTO::class.java)
                }

                override fun onError(e: Throwable) {
                    Log.e("UserFragment", "getFollowerAndroidFollowing exception: " + e.message)
                }

                override fun onComplete() {
                }

            })


//        val disposable = repository.getFollowerAndroidFollowing(uId)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                followDTO.value = it.toObject(FollowDTO::class.java)
//            }, {
//                Log.e("UserFragment", "getFirebaseStoreProfileImage exception: " + it.message)
//            })
//        disposables.add(disposable)
    }


    fun getContentDTOList(): MutableLiveData<List<ContentDTO>> = contentDTOs
    fun getFirebaseStoreProfileImageURL(): MutableLiveData<String> = profileURL
    fun getFollowerAndroidFollowingData(): MutableLiveData<FollowDTO> = followDTO

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}