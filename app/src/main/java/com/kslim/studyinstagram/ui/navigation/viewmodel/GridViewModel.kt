package com.kslim.studyinstagram.ui.navigation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class GridViewModel(private val repository: UserRepository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val contentDTOs: MutableLiveData<List<ContentDTO>> = MutableLiveData()


    fun requestFirebaseStoreItemList(uId: String) {
        repository.requestFirebaseStoreAllUserItemList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).toObservable()
            .subscribe(object : Observer<List<ContentDTO>> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: List<ContentDTO>) {
                    contentDTOs.value = t
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {

                }
            })
    }

    fun getContentDTOList(): MutableLiveData<List<ContentDTO>> = contentDTOs
    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}