package com.cyan.fireminigame.callback

import com.google.firebase.database.DataSnapshot

interface GameResultListener {
    fun getRoomInfo(
        title: String?,
        game: Int?,
        member1: String?,
        member2: String?,
        m1Ready: Int?,
        m2Ready: Int?,
        kickOut: String?,
        gameStart: String?
    )

    fun getConnectInfo(connect: String?)
}