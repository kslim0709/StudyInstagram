package com.kslim.studyinstagram.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.ViewModelProviderFactory
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.databinding.ActivityLoginBinding
import com.kslim.studyinstagram.utils.startMainActivity
import java.util.*


class LoginActivity : AppCompatActivity(), LoginListener {
    private val TAG: String = "LoginActivity"

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var googleSignInClient: GoogleSignInClient
    private val GOOGLE_LOGIN_CODE = 9001

    lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val provider =
            ViewModelProviderFactory(UserRepository.getInstance())
        loginViewModel = ViewModelProvider(this, provider).get(LoginViewModel::class.java)

        loginBinding.loginActivity = this@LoginActivity
        loginBinding.loginViewModel = loginViewModel

        loginViewModel.loginListener = this@LoginActivity

        initGoogleSignInClient()

        callbackManager = CallbackManager.Factory.create()

    }

    override fun onStart() {
        super.onStart()
        moveMainPage(UserRepository().currentUser())
    }

    fun moveMainPage(user: FirebaseUser?) {
//        if (user != null) {
//            startMainActivity()
//            finish()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle: ${account.id}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed, ${e.message}")
            }
        }

    }

    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        loginViewModel.googleLogin(credential)
    }


    fun googleLogin() {
        val signInClient = googleSignInClient.signInIntent
        startActivityForResult(signInClient, GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                }

            })
    }

    fun handleFacebookAccessToken(token: AccessToken?) {
        val credential = FacebookAuthProvider.getCredential(token?.token!!)
        loginViewModel.facebookLogin(credential)
    }

    //TMGLAcY9A8LnJGB2QFyLN6k7aBY=

    override fun onStarted() {
    }

    override fun onSuccess() {
        startMainActivity()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}