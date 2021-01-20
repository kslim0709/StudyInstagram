package com.kslim.studyinstagram

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.ui.login.LoginViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelProviderFactory() : ViewModelProvider.NewInstanceFactory() {
    private var userRepository: UserRepository? = null

    constructor(userRepository: UserRepository) : this() {
        this.userRepository = userRepository
    }


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository!!) as T
        }else{
            throw IllegalArgumentException()
        }
    }


}