package com.cyan.fireminigame.model

import android.util.Log
import com.cyan.fireminigame.callback.ChatResultListener
import com.cyan.fireminigame.callback.GameResultListener
import com.cyan.fireminigame.callback.LobbyResultListener
import com.cyan.fireminigame.callback.NbResultListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value

class EventListener {

    class GetChatListener(var chatResultListener: ChatResultListener) : ValueEventListener {
        override fun onDataChange(data: DataSnapshot) {
            chatResultListener.refreshChat(data.children)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("getChat", "fail to get chats : $error")
        }

    }

    class GetRoomListener(var lobbyResultListener: LobbyResultListener) : ValueEventListener {
        override fun onDataChange(data: DataSnapshot) {
            lobbyResultListener.refreshRoom(data.children)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("getRoom", "fail to get rooms : $error")
        }

    }

    class GetRoomInfoListener(var gameResultListener: GameResultListener) : ValueEventListener {
        override fun onDataChange(data: DataSnapshot) {
            if (data.child("title").value.toString() != "null") {
                gameResultListener.getRoomInfo(
                    data.child("title").value.toString(),
                    data.child("game").value.toString().toInt(),
                    data.child("member1").value.toString(),
                    data.child("member2").value.toString(),
                    data.child("m1Ready").value.toString().toInt(),
                    data.child("m2Ready").value.toString().toInt(),
                    data.child("kickOut").value.toString(),
                    data.child("gameStart").value.toString()
                )
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("getRoomInfo", "fail to get room information : $error")
        }

    }

    class GetConnectInfoListener(var gameResultListener: GameResultListener, var host: Boolean) :
        ValueEventListener {
        override fun onDataChange(data: DataSnapshot) {
            gameResultListener.getConnectInfo(data.value.toString())
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("getRoomInfo", "fail to get room connection information : $error")
        }

    }

    class GetNbInfoListener(var nbResultListener: NbResultListener) : ValueEventListener {
        override fun onDataChange(data: DataSnapshot) {
            nbResultListener.getNbInfo(
                data.child("m1Num").value.toString(),
                data.child("m2Num").value.toString(),
                data.child("m1Submit").value.toString(),
                data.child("m2Submit").value.toString(),
                data.child("status").value.toString(),
                data.child("turn").value.toString()
            )
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("getRoomInfo", "fail to game(Nb) information : $error")
        }

    }

    class GetNbNotifyListener(var nbNotifyListener: NbResultListener) : ValueEventListener {
        override fun onDataChange(data: DataSnapshot) {
            nbNotifyListener.refreshNotify(data.children)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("getRoomInfo", "fail to game(Nb) notify : $error")
        }

    }
}