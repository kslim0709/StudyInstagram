package com.kslim.studyinstagram

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.ui.login.LoginViewModel
import com.kslim.studyinstagram.ui.navigation.viewmodel.DetailViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelProviderFactory() : ViewModelProvider.NewInstanceFactory() {
    private var userRepository: UserRepository? = null

    constructor(userRepository: UserRepository) : this() {
        this.userRepository = userRepository
    }


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository!!) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(userRepository!!) as T
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }


}