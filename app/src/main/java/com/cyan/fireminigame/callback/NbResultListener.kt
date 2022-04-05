package com.cyan.fireminigame.callback

import com.google.firebase.database.DataSnapshot

interface NbResultListener {
    fun getNbInfo(
        m1Num: String,
        m2Num: String,
        m1Submit: String?,
        m2Submit: String?,
        status: String?,
        turn: String?,
    )

    fun getMember(member1: String, member2: String)
    fun refreshNotify(data: MutableIterable<DataSnapshot>)
}