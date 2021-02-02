package com.kslim.studyinstagram.ui.photo

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.kslim.studyinstagram.data.repository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddPhotoViewModel(private val repository: UserRepository) : ViewModel() {

    private val disposables = CompositeDisposable()

    var addPhotoListener: AddPhotoListener? = null


    fun contentUpload(photoUri: Uri, photoName: String, photoExplain: String) {

        val disposable =
            repository.uploadContents(photoUri, photoName, photoExplain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    addPhotoListener?.onSuccessUpload()
                }, {
                    addPhotoListener?.onFailUpload(it)
                })

        disposables.add(disposable)

    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}