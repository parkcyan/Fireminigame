package com.cyan.fireminigame.viewmodel.activity

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyan.fireminigame.callback.MainResultListener
import com.cyan.fireminigame.model.FireBaseManager
import com.cyan.fireminigame.model.FireBaseAuthentication
import com.google.firebase.auth.FirebaseAuth

class MainViewModel(activity: Activity) : ViewModel(), MainResultListener {

    var siStatus = MutableLiveData<Boolean>()
    var nick = MutableLiveData<String>()

    private val authUser = FirebaseAuth.getInstance()
    private val user = authUser.currentUser
    private val fs = FireBaseManager()
    private val auth = FireBaseAuthentication(activity)

    init {
        auth.getSignInStatus(this)
        if (user != null) {
            fs.getNick(user.email, this)
        }
    }

    fun signOut() {
        authUser.signOut()
    }

    // fs.getNick()
    override fun getNick(n: String) {
        nick.value = n
    }

    // auth.getSignInStatus()
    override fun getSignInStatus(status: Boolean) {
        siStatus.value = status
    }

}


