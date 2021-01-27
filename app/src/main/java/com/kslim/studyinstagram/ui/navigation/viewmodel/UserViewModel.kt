package com.kslim.studyinstagram.ui.navigation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.ui.navigation.model.FollowDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val contentDTOs: MutableLiveData<List<ContentDTO>> = MutableLiveData()
    private val profileURL: MutableLiveData<String> = MutableLiveData()
    private val followDTO: MutableLiveData<FollowDTO> = MutableLiveData()

    fun requestFirebaseStoreItemList(uId: String) {
        val disposable = repository.requestFirebaseStoreUserItemList(uId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                contentDTOs.value = it
            }, {
                Log.e("UserFragment", "requestFirebaseStoreItemList exception: " + it.message)
            })

        disposables.add(disposable)
    }

    fun getFirebaseStoreProfileImage(uId: String) {
        val disposable = repository.getFirebaseStoreProfileImage(uId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val url = it.data!!["image"]
                profileURL.value = url as String
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
        val disposable = repository.getFollowerAndroidFollowing(uId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                followDTO.value = it.toObject(FollowDTO::class.java)
            }, {
                Log.e("UserFragment", "getFirebaseStoreProfileImage exception: " + it.message)
            })
        disposables.add(disposable)
    }


    fun getContentDTOList(): MutableLiveData<List<ContentDTO>> = contentDTOs
    fun getFirebaseStoreProfileImageURL(): MutableLiveData<String> = profileURL
    fun getFollowerAndroidFollowingData(): MutableLiveData<FollowDTO> = followDTO

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}