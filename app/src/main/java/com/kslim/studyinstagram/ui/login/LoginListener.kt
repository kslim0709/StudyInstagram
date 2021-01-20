package com.kslim.studyinstagram.ui.login

interface LoginListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}