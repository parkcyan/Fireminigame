package com.cyan.fireminigame.callback

import com.google.firebase.database.DataSnapshot

interface ChatResultListener {
    fun refreshChat(data: MutableIterable<DataSnapshot>)
}