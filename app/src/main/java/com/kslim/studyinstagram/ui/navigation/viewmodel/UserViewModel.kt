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
    }

    fun getFirebaseStoreProfileImage(uId: String) {
        repository.getFirebaseStoreProfileImage(uId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).toObservable()
            .subscribe(object : Observer<DocumentSnapshot> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: DocumentSnapshot) {
                    Log.d("UserFragment", "UserFragment profileImage: ${t.data}")
                    if (t.data != null) {
                        val url = t.data!!["image"]
                        profileURL.value = url as String
                    }
                }

                override fun onError(e: Throwable) {
                    Log.e("UserFragment", "getFirebaseStoreProfileImage exception: " + e.message)
                }

                override fun onComplete() {
                }

            })
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
                    followDTO.value = t.toObject(FollowDTO::class.java)
                }

                override fun onError(e: Throwable) {
                    Log.e("UserFragment", "getFollowerAndroidFollowing exception: " + e.message)
                }

                override fun onComplete() {
                }

            })
    }

    fun getContentDTOList(): MutableLiveData<List<ContentDTO>> = contentDTOs
    fun getFirebaseStoreProfileImageURL(): MutableLiveData<String> = profileURL
    fun getFollowerAndroidFollowingData(): MutableLiveData<FollowDTO> = followDTO

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}