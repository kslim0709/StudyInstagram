package com.kslim.studyinstagram.ui.navigation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val contentDTOs: MutableLiveData<List<ContentDTO>> = MutableLiveData()

    fun requestFirebaseStoreItemList() {
        val disposable = repository.requestFirebaseStoreItemList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                contentDTOs.value = it
            }, {
                Log.e("DetailViewFragment", "requestFirebaseStoreItemList exception: " + it.message)
            })

        disposables.add(disposable)
    }

    fun updateFavoriteEvent(uId: String) {
        val disposable = repository.updateFavoriteEvent(uId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.v("DetailViewFragment", "updateFavoriteEvent: success")
            }, {
                Log.e("DetailViewFragment", "updateFavoriteEvent exception: " + it.message)
            })

        disposables.add(disposable)

    }


    fun getContentDTOList(): MutableLiveData<List<ContentDTO>> = contentDTOs


    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}