package com.cyan.fireminigame.viewmodel.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyan.fireminigame.callback.GameResultListener
import com.cyan.fireminigame.model.FireBaseManager
import com.cyan.fireminigame.model.FireBaseManager.Companion.NOT_READY
import com.cyan.fireminigame.model.FireBaseManager.Companion.READY
import java.util.*
import kotlin.concurrent.timer

class GameViewModel(private val key: String, private val host: Boolean) : ViewModel(),
    GameResultListener {

    companion object {
        const val KICKED_OUT = 0 // 강퇴 (미구현)
        const val HOST_LEAVES = 1
        const val CONNECTION_FAIL = 2

        const val NO_OPPONENT = 11
        const val NOT_HOST = 12
    }

    private var connectCheck = 0
    var title: String? = "" // 방 제목
    var member1: String? = "" // host 의 닉네임
    var member2: String? = "" // guest 의 닉네임
    var mReady = NOT_READY // 본인의 레디 상태를 담는 변수
    var game = MutableLiveData<Int>() // 플레이할 게임
    var m1Ready = MutableLiveData(NOT_READY) // 방장의 레디 상태를 담는 변수
    var m2Ready = MutableLiveData(NOT_READY) // 상대방의 레디상태를 담는 변수
    var outRoom = MutableLiveData<Boolean>() // 방에서 나가기
    var kickedOutRoom = MutableLiveData<Int>()  // 방에서 내보내진 사유
    var gameStart = MutableLiveData<Int>()

    private val fs = FireBaseManager()

    init {
        // 게임방의 정보를 불러들이고 갱신함
        fs.getRoomInfo(key, this)
        fs.getConnect(key, host, this)
        timer(period = 1000, initialDelay = 1000) {
            if (outRoom.value == true) cancel()
            if (member2 != "") connectCheck++
            if (connectCheck == 5) {
                connectionError()
            }
        }
        timer(period = 2000, initialDelay = 1000) {
            if (outRoom.value == true) cancel()
            fs.sendConnect(key, host)
        }
    }

    private fun connectionError() {
        if (host && (gameStart.value == null || gameStart.value!! < 5000)) {
            fs.kickOutGuest(key)
        } else if (!host && (gameStart.value == null || gameStart.value!! < 5000)) {
            outRoom()
            kickedOutRoom.value = CONNECTION_FAIL
        } else if(gameStart.value!! >= 5000) {
            fs.connectionFailOnPlaying(key, host)
        }
    }

    // 방장의 레디버튼 클릭
    fun btnM1Ready() {
        if (host) {
            mReady = if (m1Ready.value == NOT_READY) READY
            else NOT_READY
            fs.setReady(key, mReady, host)
        }
    }

    // 상대방의 레디버튼 클릭
    fun btnM2Ready() {
        if (!host) {
            mReady = if (m2Ready.value == NOT_READY) READY
            else NOT_READY
            fs.setReady(key, mReady, host)
        }
    }

    // 게임 시작 버튼
    fun btnGameStart() {
        if (!host) {
            gameStart.value = NOT_HOST
        } else if (member2 == "") {
            gameStart.value = NO_OPPONENT
        } else if (m1Ready.value != READY || m2Ready.value != READY) {
            gameStart.value = NOT_READY
        } else {
            fs.gameStart(key, game.value, member1, member2)
        }

    }

    // 방에서 나갈때
    fun outRoom() {
        fs.removeGetRoomInfo()
        fs.removeGetConnectInfo()
        outRoom.postValue(true)
        fs.outRoom(key, host, this)
    }

    // fs.getRoomInfo()
    override fun getRoomInfo(
        title: String?,
        game: Int?,
        member1: String?,
        member2: String?,
        m1Ready: Int?,
        m2Ready: Int?,
        kickOut: String?,
        gameStart: String?,
    ) {
        if (kickOut == "HOST_LEAVE" && !host) {
            fs.removeGetRoomInfo()
            fs.removeGetConnectInfo()
            outRoom.value = true
            kickedOutRoom.value = HOST_LEAVES
        } else if (gameStart == "START") {
            this.gameStart.value = this.game.value
        } else {
            this.title = title
            this.game.value = game
            this.member1 = member1
            if (member2 == null) this.member2 = ""
            else this.member2 = member2
            this.m1Ready.value = m1Ready
            this.m2Ready.value = m2Ready
        }
        if (member2 == "") connectCheck = 0
    }

    override fun getConnectInfo(connect: String?) {
        if (connect != null) {
            if (connect.contains("OK")) connectCheck = 0
        }
    }
}