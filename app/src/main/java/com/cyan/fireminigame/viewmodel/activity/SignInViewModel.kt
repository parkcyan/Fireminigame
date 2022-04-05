package com.cyan.fireminigame.viewmodel.activity

import android.app.Activity
import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyan.fireminigame.callback.SiResultListener
import com.cyan.fireminigame.model.FireBaseAuthentication
import com.google.firebase.auth.FirebaseAuth

class SignInViewModel(activity: Activity) : ViewModel(), SiResultListener {

    companion object {
        const val MIN_CLICK_INTERVAL: Long = 1500
        const val EMAIL_NOT_INPUT = 0
        const val PWD_NOT_INPUT = 1
    }

    var siResult = MutableLiveData<Int>()
    var siStatus = MutableLiveData<Boolean>()
    var suEm = MutableLiveData<Boolean>()
    var email = ""
    var pwd = ""

    private var currentClickTime: Long = 0
    private var lastClickTime: Long = 0
    private val auth = FireBaseAuthentication(activity)
    private val user = FirebaseAuth.getInstance().currentUser

    init {
        siStatus.value = user != null
    }

    fun txtSuEmOnClick() {
        suEm.value = true
    }

    fun btnSiOnClick() {
        when {
            email == "" -> {
                siResult.value = EMAIL_NOT_INPUT
            }
            pwd == "" -> {
                siResult.value = PWD_NOT_INPUT
            }
            timeCheck() -> {
                auth.signIn(email, pwd, this)
            }
        }
    }

    private fun timeCheck(): Boolean {
        currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime: Long = currentClickTime - lastClickTime
        lastClickTime = currentClickTime
        return elapsedTime > MIN_CLICK_INTERVAL
    }

    override fun getSiResult(siRes: Int) {
        siResult.value = siRes
    }

}