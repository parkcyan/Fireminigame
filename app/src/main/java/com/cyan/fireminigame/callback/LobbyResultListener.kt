package com.cyan.fireminigame.callback

import com.google.firebase.database.DataSnapshot

interface LobbyResultListener {
    fun roomCreated()
    fun refreshRoom(data: MutableIterable<DataSnapshot>)
    fun intoRoomResult(res: Boolean)

    interface RvIntoRoom{
        fun rvIntoRoom(key:String)
    }
}