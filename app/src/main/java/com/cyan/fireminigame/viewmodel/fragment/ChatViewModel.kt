package com.cyan.fireminigame.viewmodel.fragment

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.cyan.fireminigame.adapter.ChatAdapter
import com.cyan.fireminigame.callback.ChatResultListener
import com.cyan.fireminigame.model.FireBaseManager
import com.cyan.fireminigame.model.Chat
import com.cyan.fireminigame.model.FireBaseAuthentication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot

class ChatViewModel(var nick:String) : ViewModel(), ChatResultListener {

    var chat = ""
    var chatData = MutableLiveData<ArrayList<Chat>>()
    private val fs = FireBaseManager()

    init {
        fs.getChat(this)
    }

    fun btnSendOnclick() {
        fs.sendChat(Chat(nick, chat))
    }

    fun stopRefreshChat(){
        fs.removeGetChat()
    }

    override fun refreshChat(data: MutableIterable<DataSnapshot>) {
        val db = ArrayList<Chat>()
        for (c in data) {
            db.add(
                Chat(c.child("nick").value.toString(),
                c.child("chat").value.toString())
            )
        }
        chatData.value = db
    }

}