package com.cyan.fireminigame.model

import android.app.Activity
import android.util.Log
import com.cyan.fireminigame.callback.MainResultListener
import com.cyan.fireminigame.callback.SiResultListener
import com.cyan.fireminigame.callback.SuResultListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.Exception

class FireBaseAuthentication(private var activity: Activity) {

    companion object {
        const val SIGN_UP_SUCCESS: Int = 1000
        const val ERROR_NICK_ALREADY_IN_USE: Int = 1001
        const val ERROR_EMAIL_ALREADY_IN_USE: Int = 1002
        const val SIGN_UP_FAIL = 1002
        const val SIGN_IN_SUCCESS: Int = 2000
        const val ERROR_USER_NOT_FOUND: Int = 2001
        const val ERROR_PASSWORD_NOT_MATCH: Int = 2002
        const val SIGN_IN_FAIL: Int = 2003
    }

    private var auth: FirebaseAuth = Firebase.auth
    private var fs = FireBaseManager()

    fun signUp(email: String, pwd: String, nick: String, suResultListener: SuResultListener) {
        if (email.isNotEmpty() && pwd.isNotEmpty() && nick.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        fs.userAdd(User(email, nick))
                        auth.signInWithEmailAndPassword(email, pwd)
                        // 회원 가입 후 바로 그 계정으로 로그인함
                        suResultListener.getSuResult(SIGN_UP_SUCCESS)
                        Log.d("FireBaseAuth", "sign up success")
                    } else {
                        try {
                            it.result
                        } catch (e: Exception) {
                            if (e.message.toString().contains("already in use")) {
                                suResultListener.getSuResult(ERROR_EMAIL_ALREADY_IN_USE)
                                Log.d("FireBaseAuth", "already in use")
                            } else {
                                suResultListener.getSuResult(SIGN_UP_FAIL)
                                Log.d("FireBaseAuth", e.message.toString())
                            }
                        }
                    }
                }
        }
    }

    fun signIn(email: String, pwd: String, siResultListener: SiResultListener) {
        if (email.isNotEmpty() && pwd.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        siResultListener.getSiResult(SIGN_IN_SUCCESS)
                        Log.d("firebase", "sign in success")
                    } else {
                        try {
                            it.result
                        } catch (e: Exception) {
                            when {
                                e.message.toString().contains("no user record") -> {
                                    siResultListener.getSiResult(ERROR_USER_NOT_FOUND)
                                }
                                e.message.toString().contains("password is invalid") -> {
                                    siResultListener.getSiResult(ERROR_PASSWORD_NOT_MATCH)
                                }
                                else -> {
                                    siResultListener.getSiResult(SIGN_IN_FAIL)
                                    Log.d("firebase", "sign in fail // " + e.message.toString())
                                }
                            }
                        }
                    }
                }
        }
    }

    fun getSignInStatus(mainResultListener: MainResultListener) {
        auth.addAuthStateListener {
            mainResultListener.getSignInStatus(it.currentUser!=null)
        }
    }


}