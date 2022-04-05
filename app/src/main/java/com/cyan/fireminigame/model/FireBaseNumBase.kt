package com.cyan.fireminigame.model

import android.util.Log
import com.cyan.fireminigame.callback.NbResultListener
import com.google.firebase.database.FirebaseDatabase

class FireBaseNumBase(private val key: String, private val host: Boolean) {

    private val fbDb =
        FirebaseDatabase.getInstance("https://fireminigame-default-rtdb.asia-southeast1.firebasedatabase.app")

    private lateinit var getNbInfoListener: EventListener.GetNbInfoListener
    private lateinit var getNbNotifyListener: EventListener.GetNbNotifyListener

    fun getNbInfo(nbResultListener: NbResultListener) {
        getNbInfoListener = EventListener.GetNbInfoListener(nbResultListener)
        fbDb.reference.child("NumBase").child(key)
            .addValueEventListener(getNbInfoListener)
    }

    fun getMember(nbResultListener: NbResultListener) {
        fbDb.reference.child("NumBase").child(key).get()
            .addOnSuccessListener {
                nbResultListener.getMember(
                    it.child("member1").value.toString(),
                    it.child("member2").value.toString())
            }
    }

    fun sendNotify(notify: NbNotify) {
        val data = hashMapOf(
            "notify" to notify.notify
        )
        fbDb.reference.child("NumBase").child(key).child("notify").push().setValue(data)
    }

    fun getNotify(nbResultListener: NbResultListener) {
        getNbNotifyListener = EventListener.GetNbNotifyListener(nbResultListener)
        fbDb.reference.child("NumBase").child(key).child("notify")
            .addValueEventListener(getNbNotifyListener)
    }

    fun submitNum(num: String) {
        if (host) {
            fbDb.reference.child("NumBase").child(key).child("m1Num").setValue(num)
        } else {
            fbDb.reference.child("NumBase").child(key).child("m2Num").setValue(num)
        }
    }

    fun startNb() {
        fbDb.reference.child("NumBase").child(key).child("turn").setValue("1")
        fbDb.reference.child("NumBase").child(key).child("status").setValue("NEXT_TURN")
    }

    fun waitSubmit() {
        fbDb.reference.child("NumBase").child(key).child("status").setValue("WAIT_SUBMIT")
    }

    fun nextTurn(turn: Int) {
        fbDb.reference.child("NumBase").child(key).child("turn").setValue(turn.toString())
        fbDb.reference.child("NumBase").child(key).child("status").setValue("NEXT_TURN")
    }


}