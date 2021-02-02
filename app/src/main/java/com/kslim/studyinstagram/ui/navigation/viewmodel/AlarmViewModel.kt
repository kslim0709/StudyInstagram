package com.kslim.studyinstagram.ui.navigation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.ui.navigation.model.AlarmDTO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AlarmViewModel(private val repository: UserRepository) : ViewModel() {
    private val disposables = CompositeDisposable()
    private var alarmDTOMutableLiveData: MutableLiveData<List<AlarmDTO>> = MutableLiveData()

    fun requestFirebaseStoreUserAlarmList() {
        val uId = repository.currentUser()?.uid
        repository.requestFirebaseStoreUserAlarmList(uId!!).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
            .subscribe(object : io.reactivex.Observer<List<AlarmDTO>> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: List<AlarmDTO>) {
                    alarmDTOMutableLiveData.value = t
                }

                override fun onError(e: Throwable) {
                    Log.e(
                        "AlarmViewModel",
                        "requestFirebaseStoreUserAlarmList exception: ${e.message}"
                    )
                }

                override fun onComplete() {

                }

            })
    }

    fun getAlarmDTOList(): MutableLiveData<List<AlarmDTO>> = alarmDTOMutableLiveData
    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}