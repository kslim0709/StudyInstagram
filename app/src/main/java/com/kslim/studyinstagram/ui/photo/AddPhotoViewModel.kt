package com.kslim.studyinstagram.ui.photo

import androidx.lifecycle.ViewModel
import com.kslim.studyinstagram.data.repository.UserRepository
import io.reactivex.disposables.CompositeDisposable

class AddPhotoViewModel(private val repository: UserRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    override fun onCleared() {
        super.onCleared()
    }
}