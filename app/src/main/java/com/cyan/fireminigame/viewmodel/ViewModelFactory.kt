package com.cyan.fireminigame.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyan.fireminigame.viewmodel.activity.*
import com.cyan.fireminigame.viewmodel.fragment.ChatViewModel
import com.cyan.fireminigame.viewmodel.fragment.LobbyViewModel

class ViewModelFactory {

    class WithActivity(private val activity: Activity) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
                return SignInViewModel(activity) as T
            } else if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                return SignUpViewModel(activity) as T
            } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(activity) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }

    class WithNick(private val nick: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(nick) as T
            } else if (modelClass.isAssignableFrom(LobbyViewModel::class.java)) {
                return LobbyViewModel(nick) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }

    class WithKey(private val key: String, private val host:Boolean) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                return GameViewModel(key, host) as T
            } else if(modelClass.isAssignableFrom(NumBaseViewModel::class.java)) {
                return NumBaseViewModel(key, host) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }

    }
}


