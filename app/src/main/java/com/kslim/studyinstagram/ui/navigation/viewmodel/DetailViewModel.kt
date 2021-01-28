package com.kslim.studyinstagram.ui.navigation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kslim.studyinstagram.data.repository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val contentDTOs: MutableLiveData<HashMap<String, List<Any>>> = MutableLiveData()

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

    fun updateFavoriteEvent(uId: String, imageUid: String) {
        val disposable = repository.updateFavoriteEvent(uId, imageUid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.v("DetailViewFragment", "updateFavoriteEvent: success")
            }, {
                Log.e("DetailViewFragment", "updateFavoriteEvent exception: " + it.message)
            })

        disposables.add(disposable)

    }



    fun getContentDTOList(): MutableLiveData<HashMap<String, List<Any>>> = contentDTOs


    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}