package com.kslim.studyinstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kslim.studyinstagram.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding

    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginBinding.activity = this@LoginActivity
        auth = FirebaseAuth.getInstance()
    }

    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(
            loginBinding.emailEdittext.text.toString(),
            loginBinding.passwordEdittext.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Createing a user account
                moveMainPage(task.result?.user)
            } else if (task.exception?.message.isNullOrEmpty()) {
                // show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                // Login if you have account
                signinEmail()
            }
        }
    }

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(
            loginBinding.emailEdittext.text.toString(),
            loginBinding.passwordEdittext.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Login
                moveMainPage(task.result?.user)
            } else {
                // Show the error Message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}