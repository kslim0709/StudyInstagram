package com.kslim.studyinstagram.ui.navigation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val contentDTOs: MutableLiveData<List<ContentDTO>> = MutableLiveData()


    fun requestFirebaseStoreItemList(uId:String) {
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



    fun getContentDTOList(): MutableLiveData<List<ContentDTO>> = contentDTOs


    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}