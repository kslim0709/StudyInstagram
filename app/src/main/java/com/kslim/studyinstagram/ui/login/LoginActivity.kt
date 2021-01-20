package com.kslim.studyinstagram.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.ViewModelProviderFactory
import com.kslim.studyinstagram.data.firebase.FirebaseApi
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.databinding.ActivityLoginBinding
import com.kslim.studyinstagram.utils.startMainActivity

class LoginActivity : AppCompatActivity(), LoginListener {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val provider =
            ViewModelProviderFactory(UserRepository(FirebaseApi()))
        loginViewModel = ViewModelProvider(this, provider).get(LoginViewModel::class.java)

        loginBinding.loginViewModel = loginViewModel

        loginViewModel.loginListener = this

    }

    override fun onStarted() {
    }

    override fun onSuccess() {
        startMainActivity()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}