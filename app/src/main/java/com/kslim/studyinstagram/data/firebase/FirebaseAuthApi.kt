package com.kslim.studyinstagram.data.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable


class FirebaseAuthApi {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun login(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                emitter.onComplete()
            } else {
                emitter.onError(it.exception!!)
            }
        }
    }

    fun googleLogin(credential: AuthCredential) = Completable.create { emitter ->
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (!emitter.isDisposed) {
                emitter.onComplete()
            } else {
                emitter.onError(it.exception!!)
            }
        }
    }

    fun facebookLogin(credential: AuthCredential) = Completable.create { emitter ->
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (!emitter.isDisposed) {
                emitter.onComplete()
            } else {
                emitter.onError(it.exception!!)
            }
        }
    }

    fun register(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful)
                    emitter.onComplete()
                else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun logout() = firebaseAuth.signOut()

    fun currentUser() = firebaseAuth.currentUser

}
