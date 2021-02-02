package com.kslim.studyinstagram.ui.photo

interface AddPhotoListener {
    fun onSuccessUpload()
    fun onFailUpload(throwable: Throwable)

}