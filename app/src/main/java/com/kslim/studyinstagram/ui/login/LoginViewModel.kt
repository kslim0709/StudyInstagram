package com.kslim.studyinstagram.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kslim.studyinstagram.data.repository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    var loginEmailText: String? = null
    var loginPasswordText: String? = null

    var loginListener: LoginListener? = null

    private val disposables = CompositeDisposable()


    // perform Login
    fun login() {
        if (loginEmailText.isNullOrEmpty() || loginPasswordText.isNullOrEmpty()) {
            loginListener?.onFailure("Invalid email or password")
            return
        }


        // authentication started
        loginListener?.onStarted()

        // calling login from repository to perform the actual authentication
        val disposable = repository.login(loginEmailText!!, loginPasswordText!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // success callback
                loginListener?.onSuccess()
            }, {
                loginListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    override fun onCleared() {
        Log.v("LoginViewModel", "onCleared")
        disposables.dispose()
        super.onCleared()
    }
}