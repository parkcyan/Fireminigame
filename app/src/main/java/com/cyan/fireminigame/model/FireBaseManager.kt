package com.cyan.fireminigame.model

import android.util.Log
import com.cyan.fireminigame.callback.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.util.*
import kotlin.time.Duration.Companion.seconds

class FireBaseManager {

    companion object {
        const val CREATE_ROOM_SUCCESS = 1000
        const val NUMBER_BASEBALL = 5000
        const val TEST_GAME1 = 5001
        const val TEST_GAME2 = 5002
        const val TEST_GAME3 = 5003
        const val NOT_READY = 0
        const val READY = 1
    }

    private lateinit var getChatListener: EventListener.GetChatListener
    private lateinit var getRoomListener: EventListener.GetRoomListener
    private lateinit var getConnectInfoListener: EventListener.GetConnectInfoListener
    private lateinit var getRoomInfoListener: EventListener.GetRoomInfoListener
    private var sec = 0

    // FiresStore Database
    private val fbFs = FirebaseFirestore.getInstance()

    // Realtime Database
    private val fbDb =
        FirebaseDatabase.getInstance("https://fireminigame-default-rtdb.asia-southeast1.firebasedatabase.app")


    // 회원가입 중에 유저정보를 FireStore 에 저장
    fun userAdd(user: User) {
        val data = hashMapOf(
            "email" to user.email,
            "nick" to user.nick
        )
        fbFs.collection("User").document(user.email).set(data)
    }

    // 이미 사용중인 닉네임인지 확인
    fun userNickAlready(n: String, suResultListener: SuResultListener) {
        var nickAlready = true
        fbFs.collection("User")
            .get()
            .addOnSuccessListener {
                for (user in it) {
                    val nick = user["nick"]
                    if (nick == n) {
                        nickAlready = true
                        break
                    }
                    nickAlready = false
                }
                suResultListener.getNickAlreadyResult(nickAlready)
            }
    }

    // 유저 닉네임을 불러옴
    fun getNick(email: String?, mainResultListener: MainResultListener) {
        if (email != null) {
            fbFs.collection("User").document(email)
                .get()
                .addOnSuccessListener {
                    mainResultListener.getNick(it["nick"] as String)
                }
        }
    }

    // 채팅 전송
    fun sendChat(chat: Chat) {
        val data = hashMapOf(
            "nick" to chat.nick,
            "chat" to chat.chat
        )
        fbDb.reference.child("Chat").child("main").push().setValue(data)
            .addOnSuccessListener {

            }

    }

    // 새로운 채팅이 올라올경우 채팅 내용을 불러옴
    fun getChat(chatResultListener: ChatResultListener) {
        getChatListener = EventListener.GetChatListener(chatResultListener)
        fbDb.reference.child("Chat").child("main")
            .addValueEventListener(getChatListener)
    }

    fun removeGetChat() {
        fbDb.reference.child("Chat").child("main").removeEventListener(getChatListener)
    }


    fun createRoom(gameRoom: GameRoom, lobbyResultListener: LobbyResultListener) {
        val data = hashMapOf(
            "title" to gameRoom.title,
            "game" to gameRoom.game,
            "member1" to gameRoom.member1,
            "member2" to "",
            "m1Ready" to NOT_READY,
            "m2Ready" to NOT_READY
        )
        fbDb.reference.child("Game").child(gameRoom.key).setValue(data)
            .addOnSuccessListener {
                lobbyResultListener.roomCreated()
            }
    }

    fun intoRoom(key: String, nick: String, lobbyResultListener: LobbyResultListener) {
        fbDb.reference.child("Game").child(key).child("member2").setValue(nick)
            .addOnCompleteListener {
                lobbyResultListener.intoRoomResult(it.isSuccessful)
            }
    }

    fun getRoom(lobbyResultListener: LobbyResultListener) {
        getRoomListener = EventListener.GetRoomListener(lobbyResultListener)
        fbDb.reference.child("Game")
            .addValueEventListener(getRoomListener)
    }

    fun removeGetRoom() {
        fbDb.reference.child("Game").removeEventListener(getRoomListener)
    }


    fun getRoomInfo(key: String, gameResultListener: GameResultListener) {
        getRoomInfoListener = EventListener.GetRoomInfoListener(gameResultListener)
        fbDb.reference.child("Game").child(key)
            .addValueEventListener(getRoomInfoListener)
    }

    fun sendConnect(key: String, host: Boolean) {
        val current = System.currentTimeMillis()
        sec = ((current/1000)%60).toInt()
        when (host) {
            true -> fbDb.reference.child("Game").child(key).child("connect")
                .child("m1Connect").setValue("OK$sec")
            false -> fbDb.reference.child("Game").child(key).child("connect")
                .child("m2Connect").setValue("OK$sec")
        }
    }

    fun getConnect(key: String, host: Boolean, gameResultListener: GameResultListener) {
        getConnectInfoListener = EventListener.GetConnectInfoListener(gameResultListener, host)
        if(host){
            fbDb.reference.child("Game").child(key).child("connect").child("m2Connect")
                .addValueEventListener(getConnectInfoListener)
        } else {
            fbDb.reference.child("Game").child(key).child("connect").child("m1Connect")
                .addValueEventListener(getConnectInfoListener)
        }

    }

    fun removeGetConnectInfo(){
        fbDb.reference.child("Game").removeEventListener(getConnectInfoListener)
    }

    fun removeGetRoomInfo() {
        fbDb.reference.child("Game").removeEventListener(getRoomInfoListener)
    }

    fun setReady(key: String, ready: Int, host: Boolean) {
        if (host)
            fbDb.reference.child("Game").child(key).child("m1Ready").setValue(ready)
        else if (!host)
            fbDb.reference.child("Game").child(key).child("m2Ready").setValue(ready)
    }

    fun kickOutGuest(key: String){
        fbDb.reference.child("Game").child(key).child("m2Ready").setValue(NOT_READY)
        fbDb.reference.child("Game").child(key).child("member2").setValue("")
    }

    fun outRoom(key: String, host: Boolean, gameResultListener: GameResultListener) {
        if (host) {
            fbDb.reference.child("Game").child(key).child("kickOut").setValue("HOST_LEAVE")
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        fbDb.reference.child("Game").child(key).removeValue()
                    } else {
                        try {
                            it.result
                        } catch (e: Exception) {
                            Log.d("firebase", "delete room fail // " + e.message.toString())
                        }
                    }
                }
        } else if (!host) {
            fbDb.reference.child("Game").child(key).child("m2Ready").setValue(NOT_READY)
            fbDb.reference.child("Game").child(key).child("member2").setValue("")
        }
    }

    fun gameStart(key: String, game: Int?, member1: String?, member2: String?) {
        when (game) {
            NUMBER_BASEBALL -> {
                val data = hashMapOf(
                    "member1" to member1,
                    "member2" to member2,
                    "m1Num" to "0",
                    "m2Num" to "0",
                    "status" to "NUMBER_SUBMIT",
                    "turn" to ""
                )
                fbDb.reference.child("NumBase").child(key).setValue(data)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            fbDb.reference.child("Game").child(key).child("gameStart")
                                .setValue("START")
                        }
                    }
            }
        }
    }

    fun connectionFailOnPlaying(key:String, host: Boolean) {

    }


}