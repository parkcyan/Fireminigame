package com.cyan.fireminigame.viewmodel.activity

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyan.fireminigame.callback.NbResultListener
import com.cyan.fireminigame.model.FireBaseNumBase
import com.cyan.fireminigame.model.NbNotify
import com.google.firebase.database.DataSnapshot

class NumBaseViewModel(key: String, private var host: Boolean) : ViewModel(), NbResultListener {

    companion object {
        const val SUBMIT_FIRST = 0
        const val GAME_START = 1
        const val SUBMIT = 2
        const val OPPO_TURN = 3
        const val NUM_SHORT = 10
        const val NUM_REDUP = 11 // 3 숫자 중에 중복된 숫자가 포함되어있음
    }

    var ball = "0"
    var strike = "0"
    var num1 = ""
    var num2 = ""
    var num3 = ""
    private var member1 = ""
    private var member2 = ""
    private var m1Num = ""
    private var m2Num = ""
    private var turn: Int? = 0
    var notify = MutableLiveData<String>()
    var notifyData = MutableLiveData<ArrayList<NbNotify>>()
    var isOut = MutableLiveData<Boolean>()
    var myTurn = MutableLiveData<Boolean>()
    var numCursor = MutableLiveData<Int>()
    var refreshNb = MutableLiveData<Boolean>()
    var numSubmit = MutableLiveData<Int>()
    var status = MutableLiveData<String>()

    private val fn = FireBaseNumBase(key, host)

    init {
        numCursor.value = 1
        this.status.value = "NUMBER_SUBMIT"
        fn.getNbInfo(this)
        fn.getNotify(this)
        fn.getMember(this)
        if (host) {
            notify.value = "NUMBER_SUBMIT"
        }
    }

    fun btnNum(n: Int) {
        refreshNb.value = true
        when (numCursor.value) {
            1 -> num1 = n.toString()
            2 -> num2 = n.toString()
            3 -> num3 = n.toString()
        }
        if (numCursor.value != 4 && numCursor.value != 0) numCursor.value = numCursor.value?.plus(1)
    }

    fun btnBack() {
        refreshNb.value = true
        when (numCursor.value) {
            2 -> num1 = ""
            3 -> num2 = ""
            4 -> num3 = ""
        }
        if (numCursor.value != 1 && numCursor.value != 0) numCursor.value =
            numCursor.value?.minus(1)
    }

    fun btnSubmit() {
        if (myTurn.value == null || myTurn.value == true) {
            if (num3 == "") {
                numSubmit.value = NUM_SHORT
            } else if (num1 == num2 || num2 == num3 || num1 == num3) {
                numSubmit.value = NUM_REDUP
            } else if (status.value == "NUMBER_SUBMIT" && numSubmit.value != SUBMIT_FIRST) {
                numSubmit.value = SUBMIT_FIRST
                fn.submitNum(num1 + num2 + num3)
                numCursor.value = 0
            } else if (status.value == "WAIT_SUBMIT") {
                numCursor.value = 0
                val array = checkNum(num1 + num2 + num3, m1Num, m2Num)
                if (array[0] == 0 && array[1] == 0) {
                    ball = "0"
                    strike = "0"
                    isOut.value = true
                } else {
                    ball = array[0].toString()
                    strike = array[1].toString()
                }
                makeNotify()
                numSubmit.value = SUBMIT
                refreshNb.value = true
                fn.nextTurn(turn!! + 1)
            }
        }
    }

    private fun makeNotify() {
        if (host) {
            if (isOut.value == true) {
                sendNotify("$member1 : $num1$num2$num3 -> OUT")
            } else {
                sendNotify("$member1 : $num1$num2$num3 -> $ball" + "B $strike" + "S")
            }
        } else if (!host) {
            if (isOut.value == true) {
                sendNotify("$member2 : $num1$num2$num3 -> OUT")
            } else {
                sendNotify("$member2 : $num1$num2$num3 -> $ball" + "B $strike" + "S")
            }
        }
    }

    fun sendNotify(notify: String) {
        fn.sendNotify(NbNotify(notify))
    }

    private fun checkNum(subNum: String, m1Num: String, m2Num: String): Array<Int> {
        val sub = subNum.toIntOrNull()
        val num = if (host) m2Num.toIntOrNull()
        else m1Num.toIntOrNull()

        val s = arrayOf(sub!! / 100, (sub % 100) / 10, sub % 10)
        val m = arrayOf(num!! / 100, (num % 100) / 10, num % 10)
        val r = arrayOf(0, 0)
        for ((digitS, i) in s.withIndex()) {
            for ((digitM, j) in m.withIndex()) {
                if (i == j && digitM == digitS) {
                    r[1] += 1
                    break
                } else if (i == j) {
                    r[0] += 1
                    break
                }
            }
        }
        return r
    }

    override fun getNbInfo(
        m1Num: String,
        m2Num: String,
        m1Submit: String?,
        m2Submit: String?,
        status: String?,
        turn: String?,
    ) {
        if (host && status == "NUMBER_SUBMIT") {
            if (m1Num != "0" && m2Num != "0") {
                fn.startNb()
            }
        }
        if (status == "NEXT_TURN") {
            myTurn.value = if (turn!!.toIntOrNull()!! % 2 == 1) host
            else !host
            if (myTurn.value!!) {
                numCursor.value = 1
                num1 = ""
                num2 = ""
                num3 = ""
                isOut.value = false
            } else numCursor.value = 0
            refreshNb.value = true
            fn.waitSubmit()
        }
        this.turn = turn!!.toIntOrNull()
        this.m1Num = m1Num
        this.m2Num = m2Num
        this.status.value = status

    }

    override fun getMember(member1: String, member2: String) {
        this.member1 = member1
        this.member2 = member2
    }

    override fun refreshNotify(data: MutableIterable<DataSnapshot>) {
        val db = ArrayList<NbNotify>()
        for (c in data) {
            db.add(
                NbNotify(c.child("notify").value.toString())
            )
        }
        notifyData.value = db
    }


}